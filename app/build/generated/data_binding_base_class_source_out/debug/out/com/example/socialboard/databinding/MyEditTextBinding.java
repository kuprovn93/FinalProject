// Generated by view binder compiler. Do not edit!
package com.example.socialboard.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.example.socialboard.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class MyEditTextBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView appwidgetText;

  private MyEditTextBinding(@NonNull RelativeLayout rootView, @NonNull TextView appwidgetText) {
    this.rootView = rootView;
    this.appwidgetText = appwidgetText;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static MyEditTextBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static MyEditTextBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.my_edit_text, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static MyEditTextBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.appwidget_text;
      TextView appwidgetText = rootView.findViewById(id);
      if (appwidgetText == null) {
        break missingId;
      }

      return new MyEditTextBinding((RelativeLayout) rootView, appwidgetText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
