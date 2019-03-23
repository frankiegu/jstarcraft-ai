package com.jstarcraft.ai.data.converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map.Entry;

import com.jstarcraft.ai.data.DataModule;
import com.jstarcraft.ai.data.attribute.QuantityAttribute;
import com.jstarcraft.ai.data.attribute.QualityAttribute;
import com.jstarcraft.core.utility.ConversionUtility;
import com.jstarcraft.core.utility.KeyValue;

import it.unimi.dsi.fastutil.ints.Int2FloatRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2FloatSortedMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;

/**
 * JDBC转换器
 * 
 * @author Birdy
 *
 */
public class JdbcConverter extends AbstractConverter<ResultSet> {

	protected JdbcConverter(Collection<QualityAttribute> discreteAttributes, Collection<QuantityAttribute> continuousAttributes) {
		super(discreteAttributes, continuousAttributes);
	}

	@Override
	public int convert(DataModule module, ResultSet iterator) {
		int count = 0;
		Int2IntSortedMap discreteFeatures = new Int2IntRBTreeMap();
		Int2FloatSortedMap continuousFeatures = new Int2FloatRBTreeMap();
		try {
			int size = iterator.getMetaData().getColumnCount();
			while (iterator.next()) {
				for (int index = 0; index < size; index++) {
					Object data = iterator.getObject(index + 1);
					if (data == null) {
						continue;
					}
					Entry<Integer, KeyValue<String, Boolean>> term = module.getOuterKeyValue(index);
					KeyValue<String, Boolean> keyValue = term.getValue();
					if (keyValue.getValue()) {
						QualityAttribute attribute = discreteAttributes.get(keyValue.getKey());
						data = ConversionUtility.convert(data, attribute.getType());
						int feature = attribute.convertValue((Comparable) data);
						discreteFeatures.put(module.getQualityInner(keyValue.getKey()) + index - term.getKey(), feature);
					} else {
						QuantityAttribute attribute = continuousAttributes.get(keyValue.getKey());
						data = ConversionUtility.convert(data, attribute.getType());
						float feature = attribute.convertValue((Number) data);
						continuousFeatures.put(module.getContinuousInner(keyValue.getKey()) + index - term.getKey(), feature);
					}
				}
				module.associateInstance(discreteFeatures, continuousFeatures);
				discreteFeatures.clear();
				continuousFeatures.clear();
				count++;
			}
		} catch (SQLException exception) {
			// TODO 处理日志.
			throw new RuntimeException(exception);
		}
		return count;
	}

}
