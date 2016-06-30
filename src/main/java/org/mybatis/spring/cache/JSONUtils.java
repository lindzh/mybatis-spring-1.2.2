package org.mybatis.spring.cache;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

	private static Log logger = LogFactory.getLog(JSONUtils.class);
	
	private static ObjectMapper objectMapper;
	static {
		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
	};
	
	public static ObjectMapper getJsonMapper() {
		return objectMapper;
	}

	public static String toJSON(Object obj) {
		try {
			return getJsonMapper().writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("toJSON",e);
		}
		return null;
	}

	public static <T> T fromJSON(String json, Class<T> clz) {
		try {
			return getJsonMapper().readValue(json, clz);
		} catch (Exception e) {
			logger.error("fromJSON",e);
		}
		return null;
	}

	public static <T> T fromJSON(String json, TypeReference<T> typeReference) {
		try {
			return getJsonMapper().readValue(json, typeReference);
		} catch (Exception e) {
			logger.error("fromJSON",e);
		}
		return null;
	}
}
