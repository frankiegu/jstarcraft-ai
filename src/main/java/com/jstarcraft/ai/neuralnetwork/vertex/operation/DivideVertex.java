package com.jstarcraft.ai.neuralnetwork.vertex.operation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.ai.math.structure.MathCalculator;
import com.jstarcraft.ai.math.structure.matrix.MathMatrix;
import com.jstarcraft.ai.neuralnetwork.MatrixFactory;
import com.jstarcraft.ai.neuralnetwork.vertex.AbstractVertex;
import com.jstarcraft.core.utility.KeyValue;

/**
 * An ElementWiseVertex is used to combine the activations of two or more layer
 * in an element-wise manner<br>
 * For example, the activations may be combined by addition, subtraction or
 * multiplication or by selecting the maximum. Addition, Average, Product and
 * Max may use an arbitrary number of input arrays. Note that in the case of
 * subtraction, only two inputs may be used. In all cases, the shape of the
 * input arrays must be identical.
 * 
 * @author Alex Black
 */
/**
 * Euclidean节点
 * 
 * <pre></pre>
 * 
 * @author Birdy
 *
 */
public class DivideVertex extends AbstractVertex {

	protected DivideVertex() {
	}

	public DivideVertex(String name, MatrixFactory factory) {
		super(name, factory);
	}

	@Override
	public void doCache(KeyValue<MathMatrix, MathMatrix>... samples) {
		super.doCache(samples);

		// 检查样本的数量是否一样
		int rowSize = samples[0].getKey().getRowSize();
		for (int position = 1; position < samples.length; position++) {
			if (rowSize != samples[position].getKey().getRowSize()) {
				throw new IllegalArgumentException();
			}
		}

		// 检查样本的维度是否一样
		int columnSize = samples[0].getKey().getColumnSize();
		for (int position = 1; position < samples.length; position++) {
			if (columnSize != samples[position].getKey().getColumnSize()) {
				throw new IllegalArgumentException();
			}
		}

		// TODO 考虑支持CompositeMatrix.
		MathMatrix outputData = factory.makeCache(rowSize, columnSize);
		outputKeyValue.setKey(outputData);
		MathMatrix innerError = factory.makeCache(rowSize, columnSize);
		outputKeyValue.setValue(innerError);
	}

	@Override
	public void doForward() {
		MathMatrix outputData = outputKeyValue.getKey();
		outputData.iterateElement(MathCalculator.PARALLEL, (scalar) -> {
			MathMatrix leftInputData = inputKeyValues[0].getKey();
			MathMatrix rightInputData = inputKeyValues[1].getKey();
			int row = scalar.getRow();
			int column = scalar.getColumn();
			float value = leftInputData.getValue(row, column) / rightInputData.getValue(row, column);
			scalar.setValue(value);
		});
		MathMatrix innerError = outputKeyValue.getValue();
		innerError.setValues(0F);
	}

	@Override
	public void doBackward() {
		MathMatrix innerError = outputKeyValue.getValue();
		innerError.iterateElement(MathCalculator.PARALLEL, (scalar) -> {
			int row = scalar.getRow();
			int column = scalar.getColumn();
			float value = scalar.getValue();
			MathMatrix leftInputError = inputKeyValues[0].getValue();
			MathMatrix rightInputError = inputKeyValues[1].getValue();
			if (leftInputError != null) {
				MathMatrix rightInputData = inputKeyValues[1].getKey();
				// TODO 使用累计的方式计算
				// TODO 需要锁机制,否则并发计算会导致Bug
				leftInputError.shiftValue(row, column, value / rightInputData.getValue(row, column));
			}
			if (rightInputError != null) {
				MathMatrix leftInputData = inputKeyValues[0].getKey();
				// TODO 使用累计的方式计算
				// TODO 需要锁机制,否则并发计算会导致Bug
				rightInputError.shiftValue(row, column, value * leftInputData.getValue(row, column));
			}
		});
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		} else {
			DivideVertex that = (DivideVertex) object;
			EqualsBuilder equal = new EqualsBuilder();
			equal.append(this.vertexName, that.vertexName);
			return equal.isEquals();
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hash = new HashCodeBuilder();
		hash.append(vertexName);
		return hash.toHashCode();
	}

	@Override
	public String toString() {
		return "MinusVertex(name=" + vertexName + ")";
	}

}
