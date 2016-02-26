/**
 * Copyright (c) 2005-2010 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: ReflectionUtils.java 1211 2010-09-10 16:20:45Z calvinxiu $
 */
package com.lezic.core.util;

import android.util.Log;

import com.lezic.core.exception.CustomException;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 反射工具类.
 * 
 * 提供访问私有变量,获取泛型类型Class, 提取集合中元素的属性, 转换字符串到对象等Util函数.
 * 
 * @author calvin
 */
public class ReflectionUtil {

	/** 是否Debug模式 */
	private static boolean isDebug = false;

	/**
	 * 调用Getter方法.
	 * 
	 * @throws CustomException
	 */
	public static Object invokeGetterMethod(Object obj, String propertyName) throws CustomException {
		String getterMethodName = "get" + StringUtils.capitalize(propertyName);
		return invokeMethod(obj, getterMethodName, new Class[] {}, new Object[] {});
	}

	/**
	 * 调用Setter方法.使用value的Class来查找Setter方法.
	 * 
	 * @throws CustomException
	 */
	public static void invokeSetterMethod(Object obj, String propertyName, Object value) throws CustomException {
		invokeSetterMethod(obj, propertyName, value, null);
	}

	/**
	 * 调用Setter方法.
	 * 
	 * @param propertyType 用于查找Setter方法,为空时使用value的Class替代.
	 * @throws CustomException
	 */
	public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType)
			throws CustomException {
		Class<?> type = propertyType != null ? propertyType : value.getClass();
		String setterMethodName = "set" + StringUtils.capitalize(propertyName);
		invokeMethod(obj, setterMethodName, new Class[] { type }, new Object[] { value });
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 * 
	 * @throws CustomException
	 */
	public static Object getFieldValue(final Object obj, final String fieldName) throws CustomException {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		Object result = null;
		try {
			result = field.get(obj);
		} catch (IllegalAccessException e) {
			Log.e("反射出错", "不可能抛出的异常{}", e);
		}
		return result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 * 
	 * @throws CustomException
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value)
			throws CustomException {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			Log.e("反射出错", "不可能抛出的异常:{}", e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 
	 * @throws CustomException
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) throws CustomException {
		if (obj == null) {
			throw new CustomException("object不能为空");
		}
		if (fieldName == null) {
			throw new CustomException("fieldName不能为空");
		}
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符. 用于一次性调用的情况.
	 * 
	 * @throws CustomException
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
			final Object[] args) throws CustomException {
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问. 如向上转型到Object仍无法找到, 返回null.
	 * 
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
	 * 
	 * @throws CustomException
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName,
			final Class<?>... parameterTypes) throws CustomException {
		if (obj == null) {
			throw new CustomException("object不能为空");
		}

		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Method method = superClass.getDeclaredMethod(methodName, parameterTypes);

				method.setAccessible(true);

				return method;

			} catch (NoSuchMethodException e) {// NOSONAR
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class. eg. public UserDao extends HibernateDao<User>
	 * 
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
	 * 
	 * 如public UserDao extends HibernateDao<User,Long>
	 * 
	 * @param clazz clazz The class to introspect
	 * @param index the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			Log.w("反射警告", clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			Log.w("反射警告", "Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			Log.w("反射警告", clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException("Reflection Exception.", e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}

	/**
	 * 调用传入对象的toString方法或反射返回对象成员变量值字符串。
	 * 
	 * @param obj 传入对象
	 * @return
	 * @author Lin Chenglin 2013-4-9
	 * @throws CustomException
	 */
	public static String toString(final Object obj) throws CustomException {

		if (obj.getClass() == Object.class || obj.getClass().isPrimitive()) {
			return obj.toString();
		}

		try {
			Method method = obj.getClass().getDeclaredMethod("toString", new Class[] {});
			if (isDebug) {
				Log.d("反射调试信息", "传入的对象实现了自己的toString方法，直接调用！");
			}
			return (String) method.invoke(obj, new Object[] {});
		} catch (NoSuchMethodException e) {
			if (isDebug) {
				Log.d("反射调试信息", "传入的对象没有实现自己的toString方法，反射获取！");
			}
			StringBuffer buf = new StringBuffer(obj.getClass().getName());
			buf.append(" [");
			Field[] fileds = obj.getClass().getDeclaredFields();// 获取所有成员变量
			int size = fileds.length;
			for (int i = 0; i < size; i++) {
				Field field = fileds[i];
				Object value = ReflectionUtil.getFieldValue(obj, field.getName());
				buf.append(field.getName() + "=" + ReflectionUtil.toString(value));
				if (i != size - 1) {
					buf.append(", ");
				}
			}
			buf.append("]");
			return buf.toString();
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}
}