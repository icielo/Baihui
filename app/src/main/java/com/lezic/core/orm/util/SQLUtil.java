package com.lezic.core.orm.util;

import com.lezic.core.orm.annotation.Column;
import com.lezic.core.orm.annotation.Id;
import com.lezic.core.orm.annotation.Table;
import com.lezic.core.exception.CustomException;
import com.lezic.core.util.ReflectionUtil;
import com.lezic.core.util.UtilData;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * 获取实体类生成的SQL工具类
 * 
 * @author Lin Chenglin
 * @date 2013-9-7
 */
public class SQLUtil {

	/**
	 * 获取表名
	 * 
	 * @param clazz
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-15
	 */
	public static String getTableName(Class<?> clazz) throws CustomException {
		Table table = clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		return table.name();
	}

	/**
	 * 删除表
	 * 
	 * @param clazz
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-15
	 */
	public static String getDropTableSQL(Class<?> clazz) throws CustomException {
		StringBuffer sql = new StringBuffer("drop table ");
		Table table = clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		sql.append(table.name());
		return sql.toString();
	}

	/**
	 * 根据实体类生成对应表的DDL
	 * 
	 * @param clazz
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getCreateTableSQL(Class<?> clazz) throws CustomException {
		StringBuffer sql = new StringBuffer("create table ");
		Table table = clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		sql.append(table.name() + "(");
		Field[] fileds = clazz.getDeclaredFields();// 获取所有成员变量
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Column column = (Column) field.getAnnotation(Column.class);
			if (column != null) {
				temp.append(field.getName() + " " + column.type() + ",");
			}
		}
		if (UtilData.isNull(temp.toString())) {
			throw new CustomException("There is no field!");
		}
		sql.append(temp.substring(0, temp.length() - 1));
		sql.append(")");
		return sql.toString();
	}

	/**
	 * 获取实体类生成的插入SQL
	 * 
	 * @param object
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getInsertSQL(Object object) throws CustomException {
		Class<?> clazz = object.getClass();
		StringBuffer sql = new StringBuffer("insert into ");
		Table table = (Table) clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		sql.append(table.name() + "(");
		Field[] fileds = clazz.getDeclaredFields();// 获取所有成员变量
		StringBuffer temp = new StringBuffer();
		StringBuffer values = new StringBuffer();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Column column = (Column) field.getAnnotation(Column.class);
			if (column != null) {
				temp.append(field.getName() + ",");
				Object o = ReflectionUtil.getFieldValue(object, field.getName());
				if (o == null) {
					values.append(o + ",");
				} else {
					if (Column.Type.TEXT == column.type()) {
						values.append("'" + o + "',");
					} else {
						values.append(o + ",");
					}
				}

			}
		}
		if (UtilData.isNull(temp.toString())) {
			throw new CustomException("There is no field!");
		}
		sql.append(temp.substring(0, temp.length() - 1));
		sql.append(") values(");
		sql.append(values.substring(0, values.length() - 1));
		sql.append(")");
		return sql.toString();
	}

	/**
	 * 获取实体类生成的更新SQL
	 * 
	 * @param object
	 * @param where 条件语句
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getUpdateSQL(Object object, String where) throws CustomException {
		Class<?> clazz = object.getClass();
		StringBuffer sql = new StringBuffer("update ");
		Table table = (Table) clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		sql.append(table.name() + " set ");
		Field[] fileds = clazz.getDeclaredFields();// 获取所有成员变量
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Column column = (Column) field.getAnnotation(Column.class);
			if (column != null) {
				Object o = ReflectionUtil.getFieldValue(object, field.getName());
				if (o == null) {
					temp.append(field.getName() + "=" + o + ",");
				} else {
					if (Column.Type.TEXT == column.type()) {
						temp.append(field.getName() + "='" + o + "',");
					} else {
						temp.append(field.getName() + "=" + o + ",");
					}
				}
			}
		}
		if (UtilData.isNull(temp.toString())) {
			throw new CustomException("There is no field!");
		}
		sql.append(temp.substring(0, temp.length() - 1));
		if (UtilData.isNotNull(where)) {
			if (where.contains("where")) {
				sql.append(" " + where);
			} else {
				sql.append(" where " + where);
			}
		}
		return sql.toString();
	}

	/**
	 * 获取实体类生成的更新SQL。不带where条件。更新全部记录
	 * 
	 * @param object
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getUpdateAllSQL(Object object) throws CustomException {
		return getUpdateSQL(object, null);
	}

	/**
	 * 根据主键获取实体类生成的更新SQL
	 * 
	 * @param object
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getUpdateByPk(Object object) throws CustomException {
		Class<?> clazz = object.getClass();
		Field[] fileds = clazz.getDeclaredFields();// 获取所有成员变量
		StringBuffer where = new StringBuffer();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Id pk = (Id) field.getAnnotation(Id.class);
			if (pk != null) {
				Object o = ReflectionUtil.getFieldValue(object, field.getName());
				if (o == null) {
					where.append(field.getName() + " is " + o + " and ");
				} else {
					Column column = (Column) field.getAnnotation(Column.class);
					if (Column.Type.TEXT == column.type()) {
						where.append(field.getName() + "='" + o + "' and ");
					} else {
						where.append(field.getName() + "=" + o + " and ");
					}
				}
			}
		}
		if (UtilData.isNull(where.toString())) {
			throw new CustomException("There is no primary key!");
		}

		return getUpdateSQL(object, where.substring(0, where.lastIndexOf(" and ")));
	}

	/**
	 * 获取实体类生成的删除SQL
	 * 
	 * @param object
	 * @param args 字段名数组
	 * @return
	 * @throws CustomException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getDeleteSQL(Object object, String[] args) throws CustomException, SecurityException,
			NoSuchFieldException {
		Class<?> clazz = object.getClass();
		StringBuffer sql = new StringBuffer("delete from ");
		Table table = (Table) clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		sql.append(table.name());
		if (UtilData.isNotEmpty(args)) {
			StringBuffer temp = new StringBuffer();
			for (int i = 0; i < args.length; i++) {
				if (UtilData.isNull(args[i])) {
					continue;
				}
				Field field = clazz.getDeclaredField(args[i]);
				Column column = (Column) field.getAnnotation(Column.class);
				if (column != null) {
					Object o = ReflectionUtil.getFieldValue(object, field.getName());
					if (o == null) {
						temp.append(field.getName() + " is " + o + " and ");
					} else {
						if (Column.Type.TEXT == column.type()) {
							temp.append(field.getName() + "='" + o + "' and ");
						} else {
							temp.append(field.getName() + "=" + o + " and ");
						}
					}
				}
			}
			if (UtilData.isNotNull(temp.toString())) {
				sql.append(" where " + temp.substring(0, temp.lastIndexOf(" and ")));
			}
		}
		return sql.toString();
	}

	/**
	 * 获取实体类生成的删除SQL
	 * 
	 * @param clazz
	 * @param where 条件语句
	 * @return
	 * @throws CustomException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getDeleteSQL(Class<?> clazz, String where) throws CustomException, SecurityException,
			NoSuchFieldException {
		StringBuffer sql = new StringBuffer("delete from ");
		Table table = (Table) clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		sql.append(table.name());
		if (UtilData.isNotNull(where)) {
			if (where.contains("where")) {
				sql.append(" " + where);
			} else {
				sql.append(" where " + where);
			}
		}
		return sql.toString();
	}

	/**
	 * 获取实体类生成的删除全部SQL
	 * 
	 * @param clazz
	 * @return
	 * @throws SecurityException
	 * @throws CustomException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getDeleteAllSQL(Class<?> clazz) throws SecurityException, CustomException,
			NoSuchFieldException {
		return getDeleteSQL(clazz, "");
	}

	/**
	 * 获取根据实体类主键删除SQL。支持多主键
	 * 
	 * @param object
	 * @return
	 * @throws CustomException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getDeleteByPkSQL(Object object) throws CustomException, SecurityException,
			NoSuchFieldException {
		String[] arr = getPkName(object.getClass());
		if (UtilData.isNull(arr)) {
			throw new CustomException("There is no primary key!");
		}
		return getDeleteSQL(object, arr);
	}

	/**
	 * 获取根据实体类主键删除SQL。只支持单主键
	 * 
	 * @param clazz
	 * @param pk
	 * @return
	 * @throws CustomException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getDeleteByPkSQL(Class<?> clazz, Serializable pk) throws CustomException,
			InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
		String[] arr = getPkName(clazz);
		if (UtilData.isNull(arr)) {
			throw new CustomException("There is no primary key!");
		}
		Object object = clazz.newInstance();
		ReflectionUtil.setFieldValue(object, arr[0], pk);
		return getDeleteSQL(object, new String[] { arr[0] });
	}

	/**
	 * 获取主键集合名
	 * 
	 * @param clazz
	 * @return
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String[] getPkName(Class<?> clazz) {
		List<String> list = new ArrayList<String>();
		Field[] fileds = clazz.getDeclaredFields();// 获取所有成员变量
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Id pk = (Id) field.getAnnotation(Id.class);
			if (pk != null) {
				list.add(field.getName());
			}
		}
		if (UtilData.isNull(list)) {
			return new String[] {};
		}
		String[] arr = new String[list.size()];
		list.toArray(arr);
		return arr;
	}

	/**
	 * 获取查询对象集合的SQL
	 * 
	 * @param object
	 * @param args
	 * @return
	 * @throws CustomException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getSQL(Object object, String[] args, int limit, int offset) throws CustomException,
			NoSuchFieldException {
		Class<?> clazz = object.getClass();
		StringBuffer sql = new StringBuffer("select * from ");
		Table table = (Table) clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		sql.append(table.name());
		if (UtilData.isNotEmpty(args)) {
			StringBuffer temp = new StringBuffer();
			for (int i = 0; i < args.length; i++) {
				if (UtilData.isNull(args[i])) {
					continue;
				}
				Field field = clazz.getDeclaredField(args[i]);
				Column column = (Column) field.getAnnotation(Column.class);
				if (column != null) {
					Object o = ReflectionUtil.getFieldValue(object, field.getName());
					if (o == null) {
						temp.append(field.getName() + " is " + o + " and ");
					} else {
						if (Column.Type.TEXT == column.type()) {
							temp.append(field.getName() + "='" + o + "' and ");
						} else {
							temp.append(field.getName() + "=" + o + " and ");
						}
					}
				}
			}
			if (UtilData.isNotNull(temp.toString())) {
				sql.append(" where " + temp.substring(0, temp.lastIndexOf(" and ")));
			}
			if (limit > 0) {
				sql.append(" limit " + limit);
			}
			if (offset > 0) {
				sql.append(" offset " + offset);
			}
		}
		return sql.toString();
	}

	/**
	 * 根据主键获取查询对象集合的SQL。支持多主键
	 * 
	 * @param object
	 * @return
	 * @throws CustomException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public static String getByPkSQL(Object object) throws CustomException, NoSuchFieldException {
		String[] arr = getPkName(object.getClass());
		if (UtilData.isNull(arr)) {
			throw new CustomException("There is no primary key!");
		}
		return getSQL(object, arr, 1, -1);
	}

	/**
	 * 根据主键获取查询对象集合的SQL。只支持单主键
	 * @param clazz
	 * @param pk
	 * @return
	 * @throws CustomException
	 * @throws NoSuchFieldException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static String getByPkSQL(Class<?> clazz, Serializable pk) throws CustomException, NoSuchFieldException,
			InstantiationException, IllegalAccessException {
		String[] arr = getPkName(clazz);
		if (UtilData.isNull(arr)) {
			throw new CustomException("There is no primary key!");
		}
		Object object = clazz.newInstance();
		ReflectionUtil.setFieldValue(object, arr[0], pk);
		return getSQL(object, new String[] { arr[0] }, 1, -1);
	}

	/**
	 * 获取select * from 表名
	 * 
	 * @param clazz
	 * @return
	 * @throws CustomException
	 * @author Lin Chenglin
	 * @date 2013-9-15
	 */
	public static String getSelectAllSQL(Class<?> clazz) throws CustomException {
		Table table = clazz.getAnnotation(Table.class);
		if (UtilData.isNull(table.name())) {
			throw new CustomException("The table name is empty!");
		}
		return "select * from " + table.name();
	}
}
