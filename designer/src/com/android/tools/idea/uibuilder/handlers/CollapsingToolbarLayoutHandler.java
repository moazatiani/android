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
package com.android.tools.idea.uibuilder.handlers;

import com.android.SdkConstants;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.android.SdkConstants.ATTR_CONTENT_SCRIM;
import static com.android.SdkConstants.ATTR_FITS_SYSTEM_WINDOWS;
import static com.android.SdkConstants.ATTR_TOOLBAR_ID;

public class CollapsingToolbarLayoutHandler extends FrameLayoutHandler {
  @Override
  @NotNull
  public List<String> getInspectorProperties() {
    return ImmutableList.of(
      ATTR_CONTENT_SCRIM,
      ATTR_TOOLBAR_ID,
      ATTR_FITS_SYSTEM_WINDOWS);
  }

  @Override
  @NotNull
  public List<String> getBaseStyles(@NotNull String tagName) {
    if (tagName.equals(SdkConstants.COLLAPSING_TOOLBAR_LAYOUT)) {
      return ImmutableList.of("Widget.Design.CollapsingToolbar");  // Notice the missing "Layout"
    }
    return super.getBaseStyles(tagName);
  }
}
