package rafalk42.domain.dao;

import java.math.BigDecimal;


public interface AccountDao
{
	String open(String description, BigDecimal initialBalance)
			throws AccountDaoInternalError;
	
	String getDescription(String accountId)
			throws AccountDaoInternalError;
	
	BigDecimal getBalance(String accountId)
			throws AccountDaoInternalError;
	
	void setBalance(String accountId, BigDecimal newBalance)
			throws AccountDaoInternalError;
	
	void close(String accountId)
			throws AccountDaoInternalError;
	
	boolean doesItExist(String accountId)
			throws AccountDaoInternalError;
}
