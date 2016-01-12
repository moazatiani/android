/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.android.tools.idea.run;

import com.android.ddmlib.IDevice;
import com.android.tools.idea.run.tasks.DebugConnectorTask;
import com.android.tools.idea.run.tasks.HotSwapTask;
import com.android.tools.idea.run.tasks.LaunchTask;
import com.android.tools.idea.run.tasks.LaunchTasksProvider;
import com.android.tools.idea.run.util.LaunchStatus;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class HotSwapTasksProvider implements LaunchTasksProvider {
  private final AndroidFacet myFacet;

  public HotSwapTasksProvider(@NotNull AndroidFacet facet) {
    myFacet = facet;
  }

  @NotNull
  @Override
  public List<LaunchTask> getTasks(@NotNull IDevice device, @NotNull LaunchStatus launchStatus, @NotNull ConsolePrinter consolePrinter) {
    return Collections.<LaunchTask>singletonList(new HotSwapTask(myFacet));
  }

  @Nullable
  @Override
  public DebugConnectorTask getConnectDebuggerTask(@NotNull LaunchStatus launchStatus) {
    return null;
  }

  @Override
  public boolean createsNewProcess() {
    return false;
  }

  @Nullable
  @Override
  public String getSuccessMessage() {
    return null; // returning null here allows us to not override the notification shown by InstantRunUserFeedback
  }
}