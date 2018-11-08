package com.david.bank;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import java.util.Date;

/**
 * time related utils
 * though looks like redundant,
 * timeService provide a chance for mocking(in DI framework, ie.Spring)
 * in unit-testing or other testing purpose
 *
 * No timezone concept here since this is for internal api
 */
@ManagedBean
@Singleton
public class TimeService {
	/**
	 * @return the current Date time
	 */
	public Date now() {
		return new Date();
	}
}
