package com.theone.a_levelwallet.persistence.impl;

import com.theone.a_levelwallet.domain.Account;
import com.theone.a_levelwallet.persistence.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.theone.a_levelwallet.persistence.AccountDAO;

/**
 * Created by lh on 2015/8/18.
 */
public class AccountDAOImpl implements AccountDAO{

    public Account getAccountByName(String name) {
        Account account = null;
        try {
            Connection conn = DBUtil.getConnection();
            PreparedStatement pStatement = conn.prepareStatement(getAccountByUsername);
            pStatement.setString(1, name);
            ResultSet resultSet = pStatement.executeQuery();
            if(resultSet.next())
            {
                account = new Account();
                account.setName(resultSet.getString(1));
                account.setPsdForLogin(null);
                account.setPhoneNumber(resultSet.getString(2));
                account.setGesturePsd(null);
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(pStatement);
            DBUtil.closeConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }



    public void addAccount(Account account) {
        try {
            Connection conn = DBUtil.getConnection();
            PreparedStatement pStatement = conn.prepareStatement(addAccount);
            pStatement.setString(1, account.getName());
            pStatement.setString(2, account.getPhoneNumber());
            pStatement.setString(3, account.getPsdForLogin());
            pStatement.setString(4, account.getGesturePsd());
            pStatement.executeUpdate();
            DBUtil.closePreparedStatement(pStatement);
            DBUtil.closeConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAccount(Account account) {
        try {
            Connection conn = DBUtil.getConnection();
            PreparedStatement pStatement = conn.prepareStatement(updateAccount);
            pStatement.setString(1, account.getName());
            pStatement.setString(2, account.getPhoneNumber());
            pStatement.setString(3, account.getPsdForLogin());
            pStatement.setString(4, account.getPsdForLogin());
            pStatement.executeUpdate();
            DBUtil.closePreparedStatement(pStatement);
            DBUtil.closeConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
