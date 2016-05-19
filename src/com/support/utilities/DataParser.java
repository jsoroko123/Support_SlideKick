package com.support.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.support.adapters.AttachAdapter;
import com.support.objects.Attachments;
import com.support.objects.CaseArea;
import com.support.objects.CaseSeverity;
import com.support.objects.CaseStatus;
import com.support.objects.CaseType;
import com.support.objects.CasesByMonth;
import com.support.objects.Client;
import com.support.objects.ClientDrop;
import com.support.objects.Details;
import com.support.objects.Notes;
import com.support.objects.PendingAttachments;
import com.support.objects.PendingCases;
import com.support.objects.SupportCases;
import com.support.objects.SupportTime;
import com.support.objects.SupportUser;
import com.support.objects.User;
import com.support.objects.UserDepartment;
import com.support.objects.UserDrop;
import com.support.objects.UserInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Jeffery on 2/8/2016.
 */
public final class DataParser {

    private static Gson mGson;
    /**
     * Gson member initialization
     */
    private static void initGson() {
        if (null == mGson) {
            mGson = new Gson();
        }
    }

    public static User getUser(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<User>() {
        }.getType();
        User details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static String convertObjectToString(Object jsonData)
            throws JsonSyntaxException {
        if (jsonData == null) {
            return null;
        }

        String json;
        Gson gson = new Gson();
        json = gson.toJson(jsonData);
        return json;
    }

    public static ArrayList<SupportCases> getSupportCases(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<SupportCases>>() {}.getType();
        ArrayList<SupportCases> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<UserInfo> getUserInfo(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<UserInfo>>() {}.getType();
        ArrayList<UserInfo> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<CaseStatus> getCaseStatuses(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<CaseStatus>>() {}.getType();
        ArrayList<CaseStatus> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<Client> getClients(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<Client>>() {}.getType();
        ArrayList<Client> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<ClientDrop> getClientDrop(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<ClientDrop>>() {}.getType();
        ArrayList<ClientDrop> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<UserDrop> getUserDrop(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<UserDrop>>() {}.getType();
        ArrayList<UserDrop> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<SupportUser> getSupportUserDrop(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<SupportUser>>() {}.getType();
        ArrayList<SupportUser> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<CasesByMonth> getCasesByMonth(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<CasesByMonth>>() {}.getType();
        ArrayList<CasesByMonth> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<CaseType> getCaseTypes(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<CaseType>>() {}.getType();
        ArrayList<CaseType> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<Attachments> getAttachments(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<Attachments>>() {}.getType();
        ArrayList<Attachments> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<CaseArea> getCaseArea(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<CaseArea>>() {}.getType();
        ArrayList<CaseArea> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<CaseSeverity> getCaseSeverity(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<CaseSeverity>>() {}.getType();
        ArrayList<CaseSeverity> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<Details> getCaseDetails(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<Details>>() {}.getType();
        ArrayList<Details> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<Notes> getNotes(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<Notes>>() {}.getType();
        ArrayList<Notes> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<UserDepartment> getUserDepartments(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<UserDepartment>>() {}.getType();
        ArrayList<UserDepartment> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<PendingCases> getPendingCases(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<PendingCases>>() {}.getType();
        ArrayList<PendingCases> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<PendingAttachments> getPendingAttachments(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<PendingAttachments>>() {}.getType();
        ArrayList<PendingAttachments> details = gson.fromJson(jsonData, collectionType);
        return details;
    }

    public static ArrayList<SupportTime> getSupportTime(String jsonData) throws JsonSyntaxException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<SupportTime>>() {}.getType();
        ArrayList<SupportTime> details = gson.fromJson(jsonData, collectionType);
        return details;
    }





}