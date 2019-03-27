package com.theone.a_levelwallet.persistence;

import com.theone.a_levelwallet.domain.Account;

/**
 * Created by lh on 2015/8/18.
 */
public interface AccountDAO {
    String getAccountByUsername="";

    String updateAccount="";

    String addAccount="";

    Account getAccountByName(String name);
    void addAccount(Account account);
    void updateAccount(Account account);
}
