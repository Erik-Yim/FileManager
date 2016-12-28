package cn.itcast.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * ClassName: ProtostuffUtils  
 * (Protostuff序列化与反序列化工具类)
 * @author zhangtian  
 * @version
 */
public class ProtostuffUtils {

	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>() ;
	
	private static <T> Schema<T> getSchema(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) cachedSchema.get(clazz) ;
		if(schema == null) {
			schema = RuntimeSchema.getSchema(clazz) ;
			if(schema != null) {
				cachedSchema.put(clazz, schema) ;
			}
		}
		return schema ;
	}
	
	/**
	 *  serializer:(序列化). 
	 *  @return_type:byte[]
	 *  @author zhangtian 
	 *  @param obj
	 *  @return
	 */
	public static <T> byte[] serializer(T obj) {
        if (obj == null) {
            throw new RuntimeException("序列化对象(" + obj + ")!");
        }
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) obj.getClass() ;
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE) ;
		
		try {
			Schema<T> schema = getSchema(clazz) ;
			return ProtobufIOUtil.toByteArray(obj, schema, buffer) ;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
	}
	
	/**
	 *  deserializer:(反序列化). 
	 *  @return_type:T
	 *  @author zhangtian 
	 *  @param data
	 *  @param clazz
	 *  @return
	 */
	public static <T> T deserializer(byte[] data, Class<T> clazz) {
		if (data == null || data.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }
		try {
			T obj = clazz.newInstance() ;
			Schema<T> schema = getSchema(clazz) ;
			ProtobufIOUtil.mergeFrom(data, obj, schema);
			return obj ;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
    public static <T> byte[] serializeList(List<T> objList) {
        if (objList == null || objList.isEmpty()) {
            throw new RuntimeException("序列化对象列表(" + objList + ")参数异常!");
        }
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
        byte[] protostuff = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
            protostuff = bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("序列化对象列表(" + objList + ")发生异常!", e);
        } finally {
            buffer.clear();
            try {
                if(bos!=null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return protostuff;
    }
    
    public static <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }
        
        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
        List<T> result = null;
        try {
            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
        } catch (IOException e) {
            throw new RuntimeException("反序列化对象列表发生异常!",e);
        }
        return result;
    }
}
