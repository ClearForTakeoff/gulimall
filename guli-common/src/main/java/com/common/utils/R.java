/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.swagger.models.auth.In;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;


	public int getCode(){
		Object code = get("code");
		return (int)code;
	}

	public R setData(Object data) {
		put("data",data);
		return this;
	}
	//返回指定类型的对象，指定名字
	public <T> T getDataByName(String name,TypeReference<T> tTypeReference){
		Object data = get(name);
		//转为json字符串
		String jsonString = JSON.toJSONString(data);
		//再把json字符串转为需要的类型
		T t = JSON.parseObject(jsonString, tTypeReference);
		return t;
	}
	//返回制定类型的对象
	public <T> T getData(TypeReference<T> tTypeReference){
		Object data = get("data");
		//转为json字符串
		String jsonString = JSON.toJSONString(data);
		//再把json字符串转为需要的类型
		T t = JSON.parseObject(jsonString, tTypeReference);
		return t;
	}

	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
