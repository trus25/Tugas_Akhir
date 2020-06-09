package com.example.hotelreservation.model;

import android.content.Context;

import com.example.hotelreservation.controller.SessionManagement;
import com.midtrans.sdk.corekit.core.LocalDataHandler;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.UserAddress;
import com.midtrans.sdk.corekit.models.UserDetail;
import com.midtrans.sdk.corekit.models.snap.CreditCard;

import java.util.ArrayList;

public class DataCustomer {
    private static String NAME ="test1";
    private static String PHONE = "1232131231";
    private static String EMAIL = "test1@gmail.com";

    public static CustomerDetails customerDetails(){
        CustomerDetails cd = new CustomerDetails();
        cd.setFirstName(NAME);
        cd.setPhone(PHONE);
        cd.setEmail(EMAIL);
        return cd;
    }

    public static TransactionRequest transactionRequest(Context c, String id, int price, int qty, String name){
        UserDetail userDetail = LocalDataHandler.readObject("user_details", UserDetail.class);
        SessionManagement sessionManagement = new SessionManagement(c);
        String username = sessionManagement.getUsername();
        String userid = sessionManagement.getSession()+"-"+username;
        String nama = sessionManagement.getUserama();
        String email = sessionManagement.getUserEmail();
        String phone = sessionManagement.getUserPhone();

        if (userDetail == null || !userDetail.getUserId().equals(userid)) {
            userDetail = new UserDetail();
            userDetail.setUserFullName(nama);
            userDetail.setEmail(email);
            userDetail.setPhoneNumber(phone);
            userDetail.setUserId(userid);

            ArrayList<UserAddress> userAddresses = new ArrayList<>();
            UserAddress userAddress = new UserAddress();
            userAddress.setAddress("Jalan Andalas Gang Sebelah No. 1");
            userAddress.setCity("Jakarta");
            userAddress.setAddressType(com.midtrans.sdk.corekit.core.Constants.ADDRESS_TYPE_BOTH);
            userAddress.setZipcode("12345");
            userAddress.setCountry("IDN");
            userAddresses.add(userAddress);
            userDetail.setUserAddresses(userAddresses);
            LocalDataHandler.saveObject("user_details", userDetail);
        }

        TransactionRequest request = new TransactionRequest(System.currentTimeMillis() + " ", price*qty);
        request.setCustomerDetails(customerDetails());
        ItemDetails details = new ItemDetails(id, price, qty , name);

        ArrayList<ItemDetails> itemDetails = new ArrayList<>();
        itemDetails.add(details);
        request.setItemDetails(itemDetails);


        CreditCard creditCardOptions = new CreditCard();
// Set to true if you want to save card to Snap
        creditCardOptions.setSaveCard(false);
// Set to true to save card token as `one click` token
// Set bank name when using MIGS channel
        creditCardOptions.setBank(BankType.MANDIRI);
// Set MIGS channel (ONLY for BCA, BRI and Maybank Acquiring bank)
        request.setCreditCard(creditCardOptions);
        return request;
    }
}
