package com.lezic.core.orm.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.lezic.core.exception.CustomException;
import com.lezic.core.orm.BaseDao;
import com.lezic.core.orm.util.SQLUtil;
import com.lezic.core.util.ReflectionUtil;

import java.io.Serializable;
import java.util.List;

public class BaseService<T> {
	protected BaseDao<T> dao;

	/** T类型 */
	protected Class<T> entityClass;

	public BaseService(Context context) {
		this.entityClass = ReflectionUtil.getSuperClassGenricType(getClass());
		dao = new BaseDao<T>(context, entityClass);
	}

	/**
	 * 新增实体对象
	 *
	 * @param entity
	 * @return
	 * @throws SQLException
	 * @throws CustomException
	 * @throws NoSuchFieldException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public T add(T entity) throws SQLException, CustomException, NoSuchFieldException, InstantiationException,
			IllegalAccessException {
		return dao.add(entity);
	}

	/**
	 * 通过主键删除实体对象
	 *
	 * @param entity
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws CustomException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public void delete(T entity) throws SQLException, SecurityException, CustomException, NoSuchFieldException {
		dao.delete(entity);
	}

	/**
	 * 通过主键删除实体对象
	 *
	 * @param pk
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws CustomException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public void delete(Serializable pk) throws SQLException, SecurityException, CustomException,
			InstantiationException, IllegalAccessException, NoSuchFieldException {
		dao.delete(pk);
	}

	/**
	 * 根据主键获取实体对象。单主键
	 *
	 * @param pk
	 * @return
	 * @throws Exception
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public T get(Serializable pk) throws Exception {
		return dao.get(pk);
	}

	/**
	 * 根据主键获取实体对象。多主键
	 *
	 * @param entity
	 * @return
	 * @throws Exception
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public T getByPk(T entity) throws Exception {
		return dao.getByPk(entity);
	}

	/**
	 * 保存实体对象
	 *
	 * @param entity
	 * @return
	 * @throws Exception
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public T save(T entity) throws Exception {
		return dao.save(entity);
	}

	/**
	 * 通过cursor转换为实体对象
	 *
	 * @param cursor
	 * @return
	 * @throws CustomException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	@SuppressWarnings("unchecked")
	public T cursorToEntity(Cursor cursor) throws CustomException, InstantiationException, IllegalAccessException,
			NoSuchFieldException {
		return dao.cursorToEntity(cursor);
	}

	/**
	 * 将实体对象转换为ContentValues
	 *
	 * @param entity
	 * @return
	 * @throws CustomException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-7
	 */
	public ContentValues entityToContentValues(T entity) throws CustomException, NoSuchFieldException {
		return dao.entityToContentValues(entity);
	}

	/**
	 * 查询结果集
	 *
	 * @param sql
	 * @param selectionArgs
	 * @return
	 * @throws CustomException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-15
	 */
	public List<T> rawQuery(String sql, String[] selectionArgs) throws CustomException, InstantiationException,
			IllegalAccessException, NoSuchFieldException {
		return dao.rawQuery(sql, selectionArgs);
	}

	/**
	 * 获取全部
	 *
	 * @return
	 * @throws CustomException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @author Lin Chenglin
	 * @date 2013-9-15
	 */
	public List<T> getAll() throws CustomException, InstantiationException, IllegalAccessException,
			NoSuchFieldException {
		return dao.rawQuery(SQLUtil.getSelectAllSQL(entityClass), null);
	}
}
