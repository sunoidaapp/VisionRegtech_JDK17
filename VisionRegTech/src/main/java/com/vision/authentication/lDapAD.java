package com.vision.authentication;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;

public class lDapAD {
	public static void main(String[] args) {
		System.out.println("Domain Authentication");
		lDapAD dapAD = new lDapAD();
			boolean isAuthentication = dapAD.authenticate();
			System.out.println(isAuthentication);
			/*if(isAuthentication){
				System.out.println("*****************************");
				System.out.println();
				System.out.println("Successfully");
				System.out.println();
				System.out.println("*****************************");
			}else
				System.out.println("Failed");*/
	}
	public boolean authenticate(){
	String username = "";
        String password = "Sunoida@123@u";
        String domainName = "inm.corp";
        String ldapServerUrls [] = null; 
        try {
			ldapServerUrls = nsLookup(domainName);
			System.out.println(ldapServerUrls.length);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
      //  System.out.println("Authenticating " + username + "@" + domainName + " through " + serverName + "." + domainName);
		if (ldapServerUrls == null || ldapServerUrls.length == 0) {
			try {
				throw new AccountException("Unable to find ldap servers");
			} catch (AccountException e) {
				e.printStackTrace();
			}
		}
		if (username == null || password == null || username.trim().length() == 0 || password.trim().length() == 0) {
			try {
				throw new FailedLoginException("Username or password is empty");
			} catch (FailedLoginException e) {
				e.printStackTrace();
			}
		}
//		int retryCount = 0;
		int currentLdapUrlIndex = 0;
			do {
//				retryCount++;
				try {
				System.out.println(currentLdapUrlIndex);
				System.out.println("Authenticating -> " + username + "@" + domainName + " through " + ldapServerUrls[currentLdapUrlIndex]);
					Hashtable<Object, Object> env = new Hashtable<Object, Object>();
					env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
					env.put(Context.PROVIDER_URL, ldapServerUrls[currentLdapUrlIndex]);
					env.put(Context.SECURITY_PRINCIPAL, username + "@" + domainName);
					env.put(Context.SECURITY_CREDENTIALS, password);
					DirContext ctx = new InitialDirContext(env);
					ctx.close();
					return true;
				} catch (NamingException exp) {
					exp.printStackTrace();
					// if the exception of type communication we can assume the AD
					// is not reachable hence retry can be attempted with next
					// available AD
					if (currentLdapUrlIndex < ldapServerUrls.length) {
						currentLdapUrlIndex++;
						/*if (currentLdapUrlIndex == ldapServerUrls.length) {
							currentLdapUrlIndex = 0;
						}*/
						continue;
					}
					return false;
				}
			} while (true);
	}
	private static String[] nsLookup(String argDomain) throws Exception {
		try {
			Hashtable<Object, Object> env = new Hashtable<Object, Object>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
			env.put("java.naming.provider.url", "dns:");
			DirContext ctx = new InitialDirContext(env);
			Attributes attributes = ctx.getAttributes(String.format("_ldap._tcp.%s", argDomain),
					new String[] { "srv" });
			// try thrice to get the KDC servers before throwing error
			for (int i = 0; i < 3; i++) {
				Attribute a = attributes.get("srv");
				if (a != null) {
					List<String> domainServers = new ArrayList<String>();
					NamingEnumeration<?> enumeration = a.getAll();
					while (enumeration.hasMoreElements()) {
						String srvAttr = (String) enumeration.next();
						// the value are in space separated 0) priority 1)
						// weight 2) port 3) server
						String values[] = srvAttr.toString().split(" ");
						domainServers.add(String.format("ldap://%s:%s", values[3], values[2]));
					}
					String domainServersArray[] = new String[domainServers.size()];
					domainServers.toArray(domainServersArray);
					return domainServersArray;
				}
			}
			throw new Exception("Unable to find srv attribute for the domain " + argDomain);
		} catch (NamingException exp) {
			throw new Exception("Error while performing nslookup. Root Cause: " + exp.getMessage(), exp);
		}
	}
}
