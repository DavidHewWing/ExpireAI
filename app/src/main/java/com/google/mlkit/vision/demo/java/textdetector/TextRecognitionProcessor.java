/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.google.mlkit.vision.demo.java.textdetector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.DateViewModel;
import com.google.mlkit.vision.demo.java.StillImageActivity;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.Element;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Processor for the text detector demo. */
public class TextRecognitionProcessor extends VisionProcessorBase<Text> {

  private static final String TAG = "TextRecProcessor";

  private final TextRecognizer textRecognizer;

  private static Context context;

  public TextRecognitionProcessor(Context context) {
    super(context);
    this.context = context;
    textRecognizer = TextRecognition.getClient();
  }

  @Override
  public void stop() {
    super.stop();
    textRecognizer.close();
  }

  @Override
  protected Task<Text> detectInImage(InputImage image) {
    return textRecognizer.process(image);
  }

  @Override
  protected void onSuccess(@NonNull Text text, @NonNull GraphicOverlay graphicOverlay) {
    Log.d(TAG, "On-device Text detection successful");
    Log.d(TAG, text.toString());
    logExtrasForTesting(text);
    graphicOverlay.add(new TextGraphic(graphicOverlay, text));
    addToDatastore(text);
  }

  private static void addToDatastore(Text text) {
    if (text != null) {
      List<String> texts = new ArrayList<>();
      for (int i = 0; i < text.getTextBlocks().size(); ++i) {
        List<Line> lines = text.getTextBlocks().get(i).getLines();
        for (int j = 0; j < lines.size(); ++j) {
          List<Element> elements = lines.get(j).getElements();
          for (int k = 0; k < elements.size(); ++k) {
            Element element = elements.get(k);
            Log.v(
                    MANUAL_TESTING_LOG,
                    String.format("Detected text element %d says: %s", k, element.getText()));
            texts.add(element.getText());
          }
        }
      }
      for (String s : texts) {
        Log.v(
                MANUAL_TESTING_LOG,
                s);
      }
    }
  }

  private void logExtrasForTesting(Text text) {
    if (text != null) {
      List<String> texts = new ArrayList<>();
      for (int i = 0; i < text.getTextBlocks().size(); ++i) {
        List<Line> lines = text.getTextBlocks().get(i).getLines();
        for (int j = 0; j < lines.size(); ++j) {
          List<Element> elements = lines.get(j).getElements();
          for (int k = 0; k < elements.size(); ++k) {
            Element element = elements.get(k);
            Log.v(
                MANUAL_TESTING_LOG,
                String.format("Detected text element %d says: %s", k, element.getText()));
            texts.add(element.getText());
          }
        }
      }
      HashMap<String, String> monthMap = new HashMap<>();
      addMonthsToMap(monthMap);

      String year = "";
      String month = "";
      String day = "";

      for (int i = 0; i < texts.size(); i++){
        String s = texts.get(i);
        Log.v(
                MANUAL_TESTING_LOG,
                s);
        if (s.length() == 4 && onlyDigits(s, 4)) {
          year = s;
          if (texts.get(i+1).length() == 2 && !onlyDigits(texts.get(i + 1),2)) {
            month = texts.get(i + 1);
          } else {
            year = "";
            continue;
          }
          day = texts.get(i + 2);
          break;
        }
      }
      StringBuilder sb = new StringBuilder();
      sb.append(year);
      sb.append("-");
      sb.append(monthMap.get(month));
      sb.append("-");
      sb.append(day);

      DateViewModel model = new ViewModelProvider((ViewModelStoreOwner) this.context).get(DateViewModel.class);
      model.setDate(sb.toString());
    }
  }

  private static boolean onlyDigits(String str, int n)
  {

    for (int i = 0; i < n; i++) {
      if (Character.isDigit(str.charAt(i))) {
        return true;
      }
      else {
        return false;
      }
    }
    return false;
  }

  private static void addMonthsToMap(HashMap<String, String> monthMap) {
    monthMap.put("JA", "01");
    monthMap.put("FE", "02");
    monthMap.put("MR", "03");
    monthMap.put("AL", "04");
    monthMap.put("MA", "05");
    monthMap.put("JN", "06");
    monthMap.put("JL", "07");
    monthMap.put("AU", "08");
    monthMap.put("SE", "09");
    monthMap.put("OC", "10");
    monthMap.put("DC", "10");
    monthMap.put("NO", "11");
    monthMap.put("ND", "11");
    monthMap.put("DE", "12");

  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.w(TAG, "Text detection failed." + e);
  }
}
