package pers.hzf.auth2.demos.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author houzhifang
 * @date 2024/4/25 15:52
 */
@Slf4j
public class JacksonUtils extends ObjectMapper {
    //单例,用于常规的序列化/反序列化
    private static final JacksonUtils intance = new JacksonUtils();
    //用于字段[下划线/驼峰]转换,私有,不提供对外访问
    private static JacksonUtils intanceSnake = new JacksonUtils();
    //负责生成树节点
    private static final JsonNodeFactory jsonNodeFactory;
    //负责输出json
    private static final JsonFactory jsonFactory;

    static {
        jsonFactory = new JsonFactory();
        jsonNodeFactory = new JsonNodeFactory(false);
        intanceSnake.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static JacksonUtils getInstance() {
        return intance;
    }

    /**
     * Include.ALWAYS       默认所有属性都参与序列化
     * Include.NON_DEFAULT  属性为默认值时,不参与序列化
     * Include.NON_NULL     属性为null时,不参与序列化
     * Include.NON_EMPTY    属性为"",null,空集合,空数组时,不参与序列化
     * Include.CUSTOM       属性为自定义条件下时,不参与序列化,示例@JsonInclude(value=JsonInclude.Include.CUSTOM,valueFilter=CustomFilter.class)
     * 示例: 使用注解标注在属性上 @JsonInclude(Include.NON_NULL)
     * 示例: 使用注解标注在属性上 @JsonIgnore,表示忽略此属性
     */
    private JacksonUtils() {
        //针对对象所有的成员属性,如果是null,不参与序列化
        this(JsonInclude.Include.NON_NULL);
    }

    private JacksonUtils(JsonInclude.Include include) {
        //1.设置输出时包含属性的风格
        if (include != null) {
            this.setSerializationInclusion(include);
        }

        //2.json反序列化
        //兼容字段名使用了单引号
        this.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        this.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        //兼容字段名没有用双引号包裹
        this.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        //允许json number 类型的数存在前导0 (like: 0001)
        this.enable(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS);
        //允许json存在 NaN, INF, -INF 作为number 类型
        this.enable(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS);
        //允许json 存在形如 // 或 /**/ 的注释
        this.enable(JsonParser.Feature.ALLOW_COMMENTS);
        //允许json生成器自动补全未匹配的括号
        this.enable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        //验证重复属性
        this.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
        //key区分大小写
        this.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        //允许将JSON中的空字符串（""）作为null值绑定到一个POJO或者Map或者Collection集合对象
        this.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);

        //3.json序列化
        //反序列化时,JSON字符串里有字段,而POJO中没有定义,会抛异常,可以设置这个来忽略未定义的字段
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //设置序列化成漂亮的JSON，而不是压缩的字符串
        //this.enable(SerializationFeature.INDENT_OUTPUT);
        //如果对象没有属性字段,会抛异常,可以设置这个来避免异常,直接序列化成{}
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        //map,list中的null值也要参与序列化
        this.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        //默认Date会序列化成时间戳，可以设置这个来序列化成`date":"2017-12-09T12:50:13.000+0000`这个样子
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString("");
            }
        });
        //进行HTML解码
        this.registerModule(new SimpleModule().addSerializer(String.class, new JsonSerializer<String>() {
            @Override
            public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(StringEscapeUtils.unescapeHtml4(value));
            }
        }));
        //设置时区getTimeZone("GMT+8:00")
        this.setTimeZone(TimeZone.getDefault());
        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 判断是否合法的json字符串
     */
    public static boolean isJSON(String jsonStr) {
        try {
            intance.readTree(jsonStr);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 创建树结构
     * ObjectNode score = JacksonUtils.createTree();
     * score.put("java", 89);
     * score.put("c++", 90);
     * ObjectNode user = JacksonUtils.createTree();
     * user.put("userName", "zhangsan");
     * user.put("score", score);
     */
    public static ObjectNode createTree() {
        return jsonNodeFactory.objectNode();
    }

    /**
     * json => JsonNode
     */
    public static JsonNode toJsonNode(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        JsonNode jsonNode = null;
        try {
            jsonNode = intance.readTree(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return jsonNode;
    }

    /**
     * list/map/bean => json
     */
    public static <T> String toJson(T obj) {
        return toJson(obj, false);
    }

    /**
     * list/map/bean => json(下划线)
     */
    public static <T> String snakeToJson(T obj) {
        return toJson(obj, false, intanceSnake);
    }

    /**
     * list/map/bean => json
     */
    public static <T> String toJson(T obj, boolean flag) {
        return toJson(obj, flag, null);
    }

    /**
     * list/map/bean => json(下划线)
     */
    public static <T> String snakeToJson(T obj, boolean flag) {
        return toJson(obj, flag, intanceSnake);
    }

    /**
     * list/map/bean => json
     */
    public static <T> String toJson(T obj, boolean flag, ObjectMapper objectMapper) {
        if (obj == null) {
            return null;
        }
        ObjectMapper mapper = objectMapper == null ? intance : objectMapper;
        try {
            if (obj instanceof String) {
                return (String) obj;
            } else {
                if (flag == true) {
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
                } else {
                    return mapper.writeValueAsString(obj);
                }
            }
        } catch (Exception e) {
            log.warn("obj:{}, message:{}", obj, e.getMessage());
            return null;
        }
    }

    /**
     * list/map/bean => json
     */
    public static <T> String toJsonDebug(T obj) {
        String json = toJson(obj);
        if (json == null) {
            //出错时,返回原始toString
            return obj.toString();
        }
        return json;
    }

    /**
     * list/map/bean => bytes
     */
    public static byte[] toBytes(Object obj) {
        byte[] array;
        try {
            array = intance.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.warn("obj:{}, message:{}", obj, e.getMessage());
            return null;
        }
        return array;
    }

    /**
     * bytes => object
     */
    public static <T> T toObject(byte[] bytes, Class<T> clazz) {
        try {
            return intance.readValue(bytes, clazz);
        } catch (IOException e) {
            log.warn("str:{}, message:{}", new String(bytes), e.getMessage());
        }
        return null;
    }

    /**
     * json => bean/简单泛型
     */
    public static <T> T toObject(String jsonStr, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonStr) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) jsonStr : intance.readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.warn("json:{}, message:{}", jsonStr, e.getMessage());
            return null;
        }
    }

    /**
     * json/bean => bean/简单泛型(单个)
     * 适合场景:
     * 1.Bean(驼峰)           => Json(下划线)        => Bean(下划线)
     * 2.Bean(下划线)          => Json(下划线)        => Bean(驼峰)
     * 3.简单泛型(驼峰/下划线)   => Json(下划线)        => List<Map下划线>
     * 原理: 先统一序列化成带下划线的json, 然后再转成Bean或简单泛型(驼峰或下划线)
     */
    public static <T> T snakeToObject(Object obj, Class<T> clazz) {
        if (obj == null || clazz == null) {
            return null;
        }
        String jsonStr = intanceSnake.toJson(obj, false, intanceSnake);
        try {
            return clazz.equals(String.class) ? (T) jsonStr : intanceSnake.readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.warn("json:{}, message:{}", jsonStr, e.getMessage());
            return null;
        }
    }

    public static <T> T snakeToObject(String jsonStr, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonStr) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) jsonStr : intanceSnake.readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.warn("json:{}, message:{}", jsonStr, e.getMessage());
            return null;
        }
    }

    /*
     * json => 复杂泛型(多个)
     * json => list： List<User> list = JacksonUtils.toObject(json, new TypeReference<List<User>>() {});
     * json => map： Map<String,User> map = JacksonUtils.toObject(json, new TypeReference<Map<String,User>>() { });
     * json => 泛型： Paginator<QueryRes> resPage = JacksonUtils.toObject(JacksonUtils.toJson(listPage), new TypeReference< Paginator<QueryRes>>() { });
     */
    public static <T> T toObject(String jsonStr, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(jsonStr) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonStr : intance.readValue(jsonStr, typeReference));
        } catch (Exception e) {
            log.warn("json:{}, message:{}", jsonStr, e.getMessage());
            return null;
        }
    }

    /**
     * json/bean => 复杂泛型(多个)
     * 适合场景:
     * 1.Bean/复杂泛型(驼峰)           => Json(下划线)        => Bean/复杂泛型(下划线)
     * 2.Bean/复杂泛型(下划线)          => Json(下划线)        => Bean/复杂泛型(驼峰)
     * 原理: 先统一序列化成带下划线的json, 然后再转成Bean或复杂泛型(驼峰或下划线)
     */
    public static <T> T snakeToObject(Object obj, TypeReference<T> typeReference) {
        if (obj == null || typeReference == null) {
            return null;
        }
        String jsonStr = intanceSnake.toJson(obj, false, intanceSnake);
        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonStr : intanceSnake.readValue(jsonStr, typeReference));
        } catch (Exception e) {
            log.warn("json:{}, message:{}", jsonStr, e.getMessage());
            return null;
        }
    }

    public static <T> T snakeToObject(String jsonStr, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(jsonStr) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonStr : intanceSnake.readValue(jsonStr, typeReference));
        } catch (Exception e) {
            log.warn("json:{}, message:{}", jsonStr, e.getMessage());
            return null;
        }
    }

    /**
     * json => 简单Map
     */
    public static Map<String, Object> toMap(String jsonStr) {
        Map<String, Object> hashMap = toObject(jsonStr, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        return hashMap;
    }

    /**
     * json => 简单List
     */
    public static List<Object> toList(String jsonStr) {
        List<Object> arrayList = toObject(jsonStr, new TypeReference<ArrayList<Object>>() {
        });
        return arrayList;
    }

    /**
     * JSONP
     */
    public static String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object), false);
    }
}
