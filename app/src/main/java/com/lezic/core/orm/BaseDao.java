package com.lezic.core.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lezic.core.orm.annotation.Column;
import com.lezic.core.orm.annotation.Table;
import com.lezic.core.orm.util.SQLUtil;
import com.lezic.core.exception.CustomException;
import com.lezic.core.util.ReflectionUtil;
import com.lezic.core.util.UtilData;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao<T> {

    private DatabaseHelper databaseHelper;

    private Class<T> entityClass;

    public BaseDao(Context context) {
        this.entityClass = ReflectionUtil.getSuperClassGenricType(getClass());
    }

    public BaseDao(Context context, Class<T> entityClass) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
        this.entityClass = entityClass;
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
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Table table = entityClass.getAnnotation(Table.class);
        if (UtilData.isNull(table.name())) {
            throw new CustomException("The table name is empty!");
        }
        long rowid = db.insert(table.name(), null, entityToContentValues(entity));
        Cursor cursor = db.rawQuery("select * from " + table.name() + " where rowid = ?", new String[]{rowid + ""});
        cursor.moveToFirst();
        entity = cursorToEntity(cursor);
        cursor.close();
        return entity;
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
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL(SQLUtil.getDeleteByPkSQL(entity));
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
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL(SQLUtil.getDeleteByPkSQL(entityClass, pk));
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLUtil.getByPkSQL(entityClass, pk), new String[]{});
        try {
            if (cursor.moveToFirst()) {
                T entity = cursorToEntity(cursor);
                cursor.close();
                return entity;
            } else {
                cursor.close();
                return null;
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            throw e;
        }
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLUtil.getByPkSQL(entity), new String[]{});
        try {
            if (cursor.moveToFirst()) {
                T e = cursorToEntity(cursor);
                cursor.close();
                return e;
            } else {
                cursor.close();
                return null;
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            throw e;
        }
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        T object = getByPk(entity);
        if (object == null) {
            entity = add(entity);
        } else {
            db.execSQL(SQLUtil.getUpdateByPk(entity));
        }
        return entity;
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
        int count = cursor.getColumnCount();
        Object entity = entityClass.newInstance();
        for (int i = 0; i < count; i++) {
            String value = cursor.getString(i);
            String fieldName = cursor.getColumnName(i);
            Class<?> type = entityClass.getDeclaredField(fieldName).getType();
            if (Boolean.class == type || boolean.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, Boolean.parseBoolean(value));
            } else if (Byte.class == type || byte.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, value.getBytes()[0]);
            } else if (byte[].class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, value.getBytes());
            } else if (Double.class == type || double.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, Double.parseDouble(value));
            } else if (Float.class == type || float.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, Float.parseFloat(value));
            } else if (Integer.class == type || int.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, Integer.parseInt(value));
            } else if (Long.class == type || long.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, Long.parseLong(value));
            } else if (Short.class == type || short.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, Short.parseShort(value));
            } else if (String.class == type) {
                ReflectionUtil.setFieldValue(entity, fieldName, value);
            } else {
                ReflectionUtil.setFieldValue(entity, fieldName, value);
            }
        }
        return (T) entity;
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
        ContentValues values = new ContentValues();
        Field[] fileds = entityClass.getDeclaredFields();// 获取所有成员变量
        for (int i = 0; i < fileds.length; i++) {
            Field field = fileds[i];
            Column column = (Column) field.getAnnotation(Column.class);
            if (column != null) {
                String fieldName = field.getName();
                Class<?> type = entityClass.getDeclaredField(fieldName).getType();
                Object value = ReflectionUtil.getFieldValue(entity, field.getName());
                if (value == null) {
                    values.putNull(fieldName);
                } else if (Boolean.class == type || boolean.class == type) {
                    values.put(fieldName, (Boolean) value);
                } else if (Byte.class == type || byte.class == type) {
                    values.put(fieldName, (Byte) value);
                } else if (byte[].class == type) {
                    values.put(fieldName, (byte[]) value);
                } else if (Double.class == type || double.class == type) {
                    values.put(fieldName, (Double) value);
                } else if (Float.class == type || float.class == type) {
                    values.put(fieldName, (Float) value);
                } else if (Integer.class == type || int.class == type) {
                    values.put(fieldName, (Integer) value);
                } else if (Long.class == type || long.class == type) {
                    values.put(fieldName, (Long) value);
                } else if (Short.class == type || short.class == type) {
                    values.put(fieldName, (Short) value);
                } else if (String.class == type) {
                    values.put(fieldName, (String) value);
                } else {
                    values.put(fieldName, (String) value);
                }
            }
        }
        return values;
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        List<T> result = new ArrayList<T>();
        while (cursor.moveToNext()) {
            result.add(this.cursorToEntity(cursor));
        }
        cursor.close();
        return result;
    }

    /**
     * 通过cursor转换为map对象
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
    public Map<String, Object> cursorToMap(Cursor cursor) throws CustomException, InstantiationException,
            IllegalAccessException, NoSuchFieldException {
        int count = cursor.getColumnCount();
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < count; i++) {
            String value = cursor.getString(i);
            String fieldName = cursor.getColumnName(i);
            Class<?> type = entityClass.getDeclaredField(fieldName).getType();
            if (Boolean.class == type || boolean.class == type) {
                map.put(fieldName, Boolean.parseBoolean(value));
            } else if (Byte.class == type || byte.class == type) {
                map.put(fieldName, value.getBytes()[0]);
            } else if (byte[].class == type) {
                map.put(fieldName, value.getBytes());
            } else if (Double.class == type || double.class == type) {
                map.put(fieldName, Double.parseDouble(value));
            } else if (Float.class == type || float.class == type) {
                map.put(fieldName, Float.parseFloat(value));
            } else if (Integer.class == type || int.class == type) {
                map.put(fieldName, Integer.parseInt(value));
            } else if (Long.class == type || long.class == type) {
                map.put(fieldName, Long.parseLong(value));
            } else if (Short.class == type || short.class == type) {
                map.put(fieldName, Short.parseShort(value));
            } else if (String.class == type) {
                map.put(fieldName, value);
            } else {
                map.put(fieldName, value);
            }
        }
        return map;
    }

    /**
     * 查询结果集。以List<Map<String,Object>返回
     *
     * @param sql
     * @param selectionArgs
     * @return
     * @throws CustomException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @author Lin Chenglin
     * @date 2013-9-16
     */
    public List<Map<String, Object>> rawQueryOfMap(String sql, String[] selectionArgs) throws CustomException,
            InstantiationException, IllegalAccessException, NoSuchFieldException {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()) {
            result.add(this.cursorToMap(cursor));
        }
        cursor.close();
        return result;
    }
}
