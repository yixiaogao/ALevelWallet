package com.theone.a_levelwallet.service;

import com.theone.a_levelwallet.domain.Account;
import com.theone.a_levelwallet.persistence.AccountDAO;
import com.theone.a_levelwallet.persistence.impl.AccountDAOImpl;

/**
 * Created by lh on 2015/8/18.
 */
public class AccountService {

    private AccountDAO accountDAO;

    public AccountService(){
        accountDAO=new AccountDAOImpl();
    }
    public Account getAccount(String name) {
        return accountDAO.getAccountByName(name);
    }

    public void addAccount(Account account) {
        accountDAO.addAccount(account);
    }

    public void updateAccount(Account account) {
        accountDAO.updateAccount(account);
    }
}

