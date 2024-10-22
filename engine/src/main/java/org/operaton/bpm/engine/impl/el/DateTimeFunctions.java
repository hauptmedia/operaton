/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.operaton.bpm.engine.impl.el;

import java.util.Date;

import org.operaton.bpm.engine.impl.util.ClockUtil;
import org.joda.time.DateTime;

/**
 * @author Sebastian Menski
 */
public class DateTimeFunctions {
  public static final String NOW = "now";
  public static final String DATE_TIME = "dateTime";

  public static Date now() {
    return ClockUtil.getCurrentTime();
  }

  public static DateTime dateTime() {
    return new DateTime(now());
  }

}