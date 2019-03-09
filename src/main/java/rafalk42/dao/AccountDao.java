package rafalk42.dao;

import java.math.BigDecimal;
import java.util.Set;


public interface AccountDao
{
	String open(String description, BigDecimal initialBalance)
			throws AccountDaoInternalError;
	
	boolean doesItExist(String accountId)
			throws AccountDaoInternalError;
	
	Set<AccountInfo> findAll()
			throws AccountDaoInternalError;
	
	AccountInfo getInfo(String accountId)
			throws AccountDaoInternalError;
	
	BigDecimal getBalance(String accountId)
			throws AccountDaoInternalError;
	
	void setBalance(String accountId, BigDecimal newBalance)
			throws AccountDaoInternalError;
	
	void close(String accountId)
			throws AccountDaoInternalError;
}
