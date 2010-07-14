/*
 * Copyright 2010 Impetus Infotech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.impetus.kundera.property.accessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.impetus.kundera.Constants;
import com.impetus.kundera.property.PropertyAccessException;

/**
 * The Class DateAccessor.
 * 
 * @author animesh.kumar
 */
public class DateAccessor extends BasePropertyAccessor<Date> {

	/** The Constant DATE_FORMATTER. */
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:S Z", Locale.ENGLISH);

	/*
	 * @see org.impetus.kundera.property.PropertyMutator#decode(byte[])
	 */
	@Override
	public Date fromBytes(byte[] bytes) throws PropertyAccessException {
		try {
			return DATE_FORMATTER.parse(new String(bytes, Constants.ENCODING));
		} catch (Exception e) {
			throw new PropertyAccessException(e.getMessage());
		}
	}

	/*
	 * @see
	 * org.impetus.kundera.property.PropertyMutator#encode(java.lang.Object)
	 */
	@Override
	public final byte[] toBytes(Date date) throws PropertyAccessException {
		try {
			return DATE_FORMATTER.format(((Date) date)).getBytes(Constants.ENCODING);
		} catch (Exception e) {
			throw new PropertyAccessException(e.getMessage());
		}
	}

}
