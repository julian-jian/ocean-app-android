package com.sky.lamp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.sky.lamp.bean.CommandLightMode;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COMMAND_LIGHT_MODE".
*/
public class CommandLightModeDao extends AbstractDao<CommandLightMode, Long> {

    public static final String TABLENAME = "COMMAND_LIGHT_MODE";

    /**
     * Properties of entity CommandLightMode.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MDeviceID = new Property(1, String.class, "mDeviceID", false, "M_DEVICE_ID");
        public final static Property MUserID = new Property(2, String.class, "mUserID", false, "M_USER_ID");
        public final static Property ModelName = new Property(3, String.class, "modelName", false, "MODEL_NAME");
        public final static Property T1 = new Property(4, String.class, "t1", false, "T1");
        public final static Property IsCustom = new Property(5, boolean.class, "isCustom", false, "IS_CUSTOM");
    }

    private DaoSession daoSession;


    public CommandLightModeDao(DaoConfig config) {
        super(config);
    }
    
    public CommandLightModeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"COMMAND_LIGHT_MODE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"M_DEVICE_ID\" TEXT," + // 1: mDeviceID
                "\"M_USER_ID\" TEXT," + // 2: mUserID
                "\"MODEL_NAME\" TEXT," + // 3: modelName
                "\"T1\" TEXT," + // 4: t1
                "\"IS_CUSTOM\" INTEGER NOT NULL );"); // 5: isCustom
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COMMAND_LIGHT_MODE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CommandLightMode entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mDeviceID = entity.getMDeviceID();
        if (mDeviceID != null) {
            stmt.bindString(2, mDeviceID);
        }
 
        String mUserID = entity.getMUserID();
        if (mUserID != null) {
            stmt.bindString(3, mUserID);
        }
 
        String modelName = entity.getModelName();
        if (modelName != null) {
            stmt.bindString(4, modelName);
        }
 
        String t1 = entity.getT1();
        if (t1 != null) {
            stmt.bindString(5, t1);
        }
        stmt.bindLong(6, entity.getIsCustom() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CommandLightMode entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mDeviceID = entity.getMDeviceID();
        if (mDeviceID != null) {
            stmt.bindString(2, mDeviceID);
        }
 
        String mUserID = entity.getMUserID();
        if (mUserID != null) {
            stmt.bindString(3, mUserID);
        }
 
        String modelName = entity.getModelName();
        if (modelName != null) {
            stmt.bindString(4, modelName);
        }
 
        String t1 = entity.getT1();
        if (t1 != null) {
            stmt.bindString(5, t1);
        }
        stmt.bindLong(6, entity.getIsCustom() ? 1L: 0L);
    }

    @Override
    protected final void attachEntity(CommandLightMode entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CommandLightMode readEntity(Cursor cursor, int offset) {
        CommandLightMode entity = new CommandLightMode( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // mDeviceID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // mUserID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // modelName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // t1
            cursor.getShort(offset + 5) != 0 // isCustom
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CommandLightMode entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMDeviceID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMUserID(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setModelName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setT1(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsCustom(cursor.getShort(offset + 5) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CommandLightMode entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CommandLightMode entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CommandLightMode entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
