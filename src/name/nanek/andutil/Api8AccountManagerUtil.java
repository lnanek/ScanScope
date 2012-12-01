package name.nanek.andutil;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;

/**
 * Gets possible user emails. Requires this permission:
 * <uses-permission android:name="android.permission.GET_ACCOUNTS" />
 *
 */
public class Api8AccountManagerUtil {
	// TODO can be done api 5 and up if use own email pattern
	
	public static String getAccountManagerEmail(final Context context) {
		List<String> emails = getAccountManagerEmails(context);
		if ( null == emails || emails.isEmpty() ) {
			return null;
		}
		
		return emails.get(0);
	}
	
	public static List<String> getAccountManagerEmails(final Context context) {
		
		final List<String> emails = new LinkedList();
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		
		Account[] accounts = AccountManager.get(context).getAccounts();
		
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        String possibleEmail = account.name;
		        emails.add(possibleEmail);
		    }
		}
		
		return emails;
	}
	
}
