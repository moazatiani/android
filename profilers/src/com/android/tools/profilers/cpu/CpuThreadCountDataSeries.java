/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.tools.profilers.cpu;

import com.android.tools.adtui.model.DataSeries;
import com.android.tools.adtui.model.Range;
import com.android.tools.adtui.model.SeriesData;
import com.android.tools.profiler.proto.CpuProfiler;
import com.android.tools.profiler.proto.CpuServiceGrpc;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for making an RPC call to perfd/datastore and converting the resulting proto into UI data.
 */
public class CpuThreadCountDataSeries implements DataSeries<Long> {
  @NotNull
  private CpuServiceGrpc.CpuServiceBlockingStub myClient;

  private final int myProcessId;

  public CpuThreadCountDataSeries(@NotNull CpuServiceGrpc.CpuServiceBlockingStub client, int id) {
    myClient = client;
    myProcessId = id;
  }

  @Override
  public ImmutableList<SeriesData<Long>> getDataForXRange(@NotNull Range timeCurrentRangeUs) {
    long bufferNs = TimeUnit.SECONDS.toNanos(1);
    CpuProfiler.GetThreadsRequest.Builder request = CpuProfiler.GetThreadsRequest.newBuilder()
      .setAppId(myProcessId)
      .setStartTimestamp(TimeUnit.MICROSECONDS.toNanos((long)timeCurrentRangeUs.getMin()) - bufferNs)
      .setEndTimestamp(TimeUnit.MICROSECONDS.toNanos((long)timeCurrentRangeUs.getMax()) + bufferNs);

    CpuProfiler.GetThreadsResponse response = myClient.getThreads(request.build());

    TreeMap<Long, Long> count = new TreeMap<>();
    for (CpuProfiler.GetThreadsResponse.Thread thread : response.getThreadsList()) {
      if (thread.getActivitiesCount() > 0) {
        CpuProfiler.GetThreadsResponse.ThreadActivity first = thread.getActivities(0);
        CpuProfiler.GetThreadsResponse.ThreadActivity last = thread.getActivities(thread.getActivitiesCount() - 1);
        Long current = count.get(first.getTimestamp());
        count.put(first.getTimestamp(), current == null ? 1 : current + 1);
        if (last.getNewState() == CpuProfiler.GetThreadsResponse.State.DEAD) {
          current = count.get(last.getTimestamp());
          count.put(last.getTimestamp(), current == null ? -1 : current - 1);
        }
      }
    }

    List<SeriesData<Long>> data = new ArrayList<>();
    long total = 0;
    for (Map.Entry<Long, Long> entry : count.entrySet()) {
      total += entry.getValue();
      data.add(new SeriesData<>(TimeUnit.NANOSECONDS.toMicros(entry.getKey()), total));
    }
    data.add(new SeriesData<>((long)timeCurrentRangeUs.getMax(), total));
    return ContainerUtil.immutableList(data);
  }
}
