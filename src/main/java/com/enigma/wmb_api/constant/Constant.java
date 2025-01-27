package com.enigma.wmb_api.constant;

public class Constant {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public static final String MENU_TABLE = "m_menu";
    public static final String USER_ACCOUNT_TABLE = "m_user_account";
    public static final String CUSTOMER_TABLE = "m_customer";
    public static final String ORDER_TABLE = "t_order";
    public static final String ORDER_DETAIL_TABLE = "t_order_detail";

    public static final String API = "/api";
    public static final String MENU_API = API + "/menus";
    public static final String USER_API = API + "/users";
    public static final String CUSTOMER_API = API + "/customers";
    public static final String AUTH_API = API + "/auth";
    public static final String ORDER_API = API + "/orders";
    public static final String PAYMENT_API = API + "/payments";
    public static final String IMAGE_API = API + "/images";

    public static final String SUCCESS_CREATE_MENU = "Successfully created new menu";
    public static final String SUCCESS_UPDATE_MENU = "Successfully updated menu";
    public static final String SUCCESS_DELETE_MENU = "Successfully deleted menu";
    public static final String SUCCESS_GET_ALL_MENU = "Successfully fetch all menu";
    public static final String SUCCESS_GET_MENU_BY_ID = "Successfully fetch menu by id";

    public static final String ERROR_USERNAME_DUPLICATE = "username already exist";
    public static final String ERROR_ROLE_NOT_FOUND = "role not found";

    public static final String SUCCESS_CREATE_USER = "Successfully created new user";
    public static final String SUCCESS_GET_USER_INFO = "Success fetch user info";
    public static final String SUCCESS_UPDATE_PASSWORD = "Success update password.";

    public static final String ERROR_CUSTOMER_NOT_FOUND = "customer not found";
    public static final String SUCCESS_GET_CUSTOMER_BY_ID = "Successfully fetch menu by id";
    public static final String SUCCESS_GET_ALL_CUSTOMER = "Successfully fetch all customer";
    public static final String SUCCESS_UPDATE_CUSTOMER = "Successfully updated menu";
    public static final String SUCCESS_DELETE_CUSTOMER = "Successfully deleted menu";
    public static final String SUCCESS_CREATE_CUSTOMER = "Successfully created new customer";

    public static final String SUCCESS_CREATE_ORDER_DRAFT = "Successfully created new order (draft)";
    public static final String SUCCESS_GET_ORDER_DETAILS = "Successfully fetched order details";
    public static final String SUCCESS_ADD_ORDER_DETAIL = "Successfully added order detail";
    public static final String SUCCESS_UPDATE_ORDER_DETAIL = "Successfully updated order detail";
    public static final String SUCCESS_REMOVE_ORDER_DETAIL = "Successfully removed order detail";
    public static final String SUCCESS_UPDATE_ORDER_STATUS = "Successfully updated order status";
    public static final String SUCCESS_CHECKOUT_ORDER = "Successfully checked out order";
    public static final String SUCCESS_GET_ALL_ORDERS = "Successfully fetched all orders";
    public static final String SUCCESS_GET_ORDER_BY_ID = "Successfully fetched order";


    public static final String SUCCESS_LOGIN = "Login Successfully";
    public static final String OK = "OK";

    public static final String ERROR_CREATE_JWT = "Error creating JWT Token";
    public static final String REFRESH_TOKEN_REQUIRED = "Refresh Token is required";


    public static final String ERROR_ADD_ITEMS_TO_NON_DRAFT = "Can only add items to draft orders";
    public static final String ERROR_UPDATE_ITEMS_IN_NON_DRAFT = "Can only update items in draft orders";
    public static final String ERROR_REMOVE_ITEMS_FROM_NON_DRAFT = "Can only remove items from draft orders";
    public static final String ERROR_ORDER_DETAIL_NOT_FOUND = "Order detail not found";
    public static final String ERROR_CHECKOUT_NON_DRAFT = "Can only checkout draft orders";
    public static final String ERROR_ORDER_NOT_FOUND = "Order not found";

    public static final String INVALID_CREDENTIAL = "Invalid Credential";
}
