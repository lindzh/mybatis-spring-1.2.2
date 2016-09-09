package org.mybatis.spring.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 * @author lindezhi
 * 2016年6月29日 下午5:29:27
 */
public class SerializeUtils {

//	private static final Logger logger = Logger.getLogger("mybatis_cache");

	public static byte[] zip(byte[] bytes){
		if(bytes!=null&&bytes.length>0){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				GZIPOutputStream gos = new GZIPOutputStream(bos);
				gos.write(bytes);
				gos.close();
				return bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new byte[0];
	}
	
	public static byte[] unzip(byte[] bytes){
		try {
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buff = new byte[512];
			int read = gis.read(buff);
			while(read>0){
				bos.write(buff,0,read);
				read = gis.read(buff);
			}
			gis.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}


	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream bis = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(bis);
			stream.writeObject(obj);
			stream.close();
			byte[] bytes = bis.toByteArray();
			//使用zip压缩，缩小网络包
			return zip(bytes);
		} catch (Exception e) {
			throw new DaoCacheException(e);
		}
	}

	public static Object deserialize(byte[] bytes) {
		try {
			//使用zip解压缩
			byte[] unzip = unzip(bytes);
			ByteArrayInputStream bis = new ByteArrayInputStream(unzip);
			ObjectInputStream stream = new ObjectInputStream(bis);
			return stream.readObject();
		} catch (Exception e) {
			throw new DaoCacheException(e);
		}
	}
}
