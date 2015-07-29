package com.example.diotek.addressmanager;

/**
 * Created by diotek on 2015-07-22.
 */
public interface BaseVariables {

    public static final String CREATE_FILE_NAME = "Address";
    public static final String DB_ADDRESS = "Address.db";
    public static final String TABLE_ADDRESS = "Address";
    public static final int DB_VERSION = 1;

    public static final String DB_INDEX = "_index";
    public static final String DB_NAME = "name";
    public static final String DB_PHONE_NUMBER = "phone_number";
    public static final String DB_HOME_NUMBER = "home_number";
    public static final String DB_COMPANY_NUMBER = "company_number";
    public static final String DB_E_MAIL = "e_mail";

    public static final int INSERT_ADDRESS_ACTIVITY = 0;
    public static final int DETAIL_ADDRESS_ACTIVITY = 1;
    public static final int MODIFY_ADDRESS_ACTIVITY = 2;
    public static final int DELETE_ADDRESS_ACTIVITY = 3;
    public static final int UPLOAD_ADDRESS_ACTIVITY = 4;
    public static final int DOWNLOAD_ADDRESS_ACTIVITY = 5;
    public static final int SYNCHRONIZE_ADDRESS_ACTIVITY = 6;
    public static final int CLOUD_ACTIVITY = 7;

    public static final int INSERT_RESULT_OK = -2;
    public static final int DELETE_RESULT_OK = -3;
    public static final int DETAIL_RESULT_OK = -4;
    public static final int MODIFY_RESULT_OK = -5;
    public static final int UPLOAD_RESULT_OK = -6;
    public static final int UPLOAD_RESULT_CANCELED = -7;
    public static final int DOWNLOAD_RESULT_OK = -8;
    public static final int DOWNLOAD_RESULT_CANCELED = -9;
    public static final int SYNCHRONIZE_RESULT_OK = -10;
    public static final int SYNCHRONIZE_RESULT_CANCELED = -11;
    public static final int CLOUD_RESULT_OK = -12;

    public static final String MY_ADDRESS = "내 연락처";
    public static final String LIST_INDEX_KEY = "individualIndex";
    public static final String LIST_POSITION_KEY = "individualPosition";
    public static final String LIST_INDIVIDUAL_DATA_KEY = "individualData";

    public static final String MODIFY_DATA_KEY = "modifyData";
    public static final String MODIFY_INDEX_KEY = "modifyIndex";
    public static final String MODIFY_POSITION_KEY = "modifyPosition";
    public static final String MODIFY_NAME_KEY = "modifyName";
    public static final String MODIFY_PHONE_NUMBER_KEY = "modifyPhoneNumber";
    public static final String MODIFY_HOME_NUMBER_KEY = "modifyHomeNumber";
    public static final String MODIFY_COMPANY_NUMBER_KEY = "modifyCompanyNumber";
    public static final String MODIFY_E_MAIL_KEY = "modifyEmail";

    public static final String MODIFIED_DATA_KEY = "modifiedData";
    public static final String MODIFIED_NAME_KEY = "modifiedName";
    public static final String MODIFIED_PHONE_NUMBER_KEY = "modifiedPhoneNumber";
    public static final String MODIFIED_HOME_NUMBER_KEY = "modifiedHomeNumber";
    public static final String MODIFIED_COMPANY_NUMBER_KEY = "modifiedCompanyNumber";
    public static final String MODIFIED_E_MAIL_KEY = "modifiedEmail";

    public static final String INSERT_DATA_KEY = "insertData";
    public static final String INSERT_INDEX_KEY = "insertIndex";
    public static final String INSERT_NAME_KEY = "insertName";

    public static final String PREFERENCE = "preference";
    public static final String DRIVE_ID = "drive_id";
    public static final String RESOURCE_ID = "resource_id";

    public static final String[] COLUMNS = new String[] {"_index", "name","phone_number","home_number","company_number", "e_mail"};

    public class Item{
        Item(long aIndex, String aName){
            mIndex = aIndex;
            mName = aName;
        }

        public long getmIndex() {
            return mIndex;
        }

        public String getmName() {
            return mName;
        }

        private long mIndex;
        private String mName;
    }

}
