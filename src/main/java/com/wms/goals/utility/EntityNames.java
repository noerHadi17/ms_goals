package com.wms.goals.utility;

public final class EntityNames {
    private EntityNames() {}

    public static final String M_GOALS = "mst_goals";

    public static final class MGoals {
        private MGoals() {}
        public static final String GOAL_ID = "goal_id";
        public static final String ID_CUSTOMER = "id_customer";
        public static final String GOAL_TYPE = "goal_type";
        public static final String TARGET_AMOUNT = "target_amount";
        public static final String TARGET_DATE = "target_date";
        public static final String ID_RISK_PROFILE = "id_risk_profile";
        public static final String GOAL_NAME = "goal_name";
        public static final String CREATED_AT = "created_at";
    }

    public static final String M_PRODUCT_TYPE = "mst_product_type";
    public static final class MProductType {
        private MProductType() {}
        public static final String PRODUCT_TYPE_ID = "product_type_id";
        public static final String PRODUCT_TYPE_NAME = "product_type_name";
        public static final String ID_RISK_PROFILE = "id_risk_profile";
    }

    public static final String M_PRODUCTS = "mst_products";
    public static final class MProducts {
        private MProducts() {}
        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_NAME = "product_name";
        public static final String PRODUCT_TYPE = "product_type";
        public static final String NAV_PRICE = "nav_price";
        public static final String CUT_OFF_TIME = "cutoff_time";
        public static final String PRODUCT_TYPE_ID = "product_type_id";
        public static final String UPDATED_AT = "updated_at";
    }

    public static final String NAV_BALANCE = "nav_balance";
    public static final class NavBalance {
        private NavBalance() {}
        public static final String NAV_ID = "nav_id";
        public static final String ID_PRODUCT = "id_product";
        public static final String NAV_PRICE = "nav_price";
        public static final String NAV_DATE = "nav_date";
        public static final String CREATED_AT = "created_at";
    }

    public static final String PORTFOLIOS = "portfolios";
    public static final class Portfolios {
        private Portfolios() {}
        public static final String PORTFOLIO_ID = "portfolio_id";
        public static final String ID_CUSTOMER = "id_customer";
        public static final String ID_GOAL = "id_goal";
        public static final String ID_PRODUCT = "id_product";
        public static final String CURRENT_UNIT = "current_unit";
    }

    public static final String MST_CUSTOMER = "mst_customer";
    public static final class MstCustomer {
        private MstCustomer() {}
        public static final String CUSTOMER_ID = "customer_id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PASSWORD_HASH = "password_hash";
        public static final String NIK = "nik";
        public static final String ADDRESS = "address";
        public static final String ID_RISK_PROFILE = "id_risk_profile";
        public static final String DOB = "dob";
        public static final String POB = "pob";
    }

    public static final String MST_RISKPROFILES = "mst_riskprofiles";
    public static final class MstRiskprofiles {
        private MstRiskprofiles() {}
        public static final String RISK_PROFILE_ID = "risk_profile_id";
        public static final String PROFILE_TYPE = "profile_type";
        public static final String SCORE_MIN = "score_min";
        public static final String SCORE_MAX = "score_max";
    }
}
