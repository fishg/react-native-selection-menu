
package ui.selectionmenu;

import android.content.Intent;
import android.graphics.Typeface;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertTheme;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.interfaces.AlertActionListener;
import com.irozon.alertview.objects.AlertAction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class RNSelectionMenuModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNSelectionMenuModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNSelectionMenu";
  }

  @ReactMethod
  public void Show(final ReadableMap props, final Callback callback) {

    ReadableArray values = props.getArray("values");
    ReadableArray selectedValues = props.getArray("selectedValues");

    int selectionType = props.getInt("selectionType");
    int presentationType = props.getInt("presentationType");

    String tickColor = props.getString("tickColor");
    String title = props.getString("title");
    String subtitle = props.getString("subtitle");
    String actionTitle = props.getString("actionTitle");
    int theme = props.getInt("theme");

    boolean enableSearch = props.getBoolean("enableSearch");
    String searchPlaceholder = props.getString("searchPlaceholder");
    String searchTintColor = props.getString("searchTintColor");

    //choose alert style
    int intAlertStyle = props.hasKey("alertStyle") ? props.getInt("alertStyle") : 0;
    AlertStyle alertStyle = AlertStyle.BOTTOM_SHEET;
    switch (intAlertStyle){
      case 1:
        alertStyle = AlertStyle.DIALOG;
        break;
      case 2:
        alertStyle = AlertStyle.IOS;
        break;
        default:
          break;
    }

    //choose alert action style
    ReadableArray actionStyles = props.hasKey("actionStyles") ? props.getArray("actionStyles") : null;

    AlertView alert ;//= new AlertView(title, subtitle, AlertStyle.BOTTOM_SHEET);
    if (presentationType == 0 || presentationType == 1) {
      if (presentationType == 0) {
        alert = new AlertView(title, subtitle, alertStyle);
      } else {
        alert = new AlertView(title, subtitle, alertStyle);
      }

      if (theme == 0) {
        alert.setTheme(AlertTheme.LIGHT);
      } else if (theme == 1) {
        alert.setTheme(AlertTheme.DARK);
      }

      for (int i = 0;i < values.size();i++) {
        AlertActionStyle style = AlertActionStyle.DEFAULT;
        if(actionStyles != null && i < actionStyles.size()){
          style = getActionStyle(actionStyles.getInt(i));
        }
        alert.addAction(new AlertAction(values.getString(i), style, new AlertActionListener() {
          @Override
          public void onActionClick(@NotNull AlertAction action) {
            callback.invoke(action.getTitle());
          }
        }));
      }

      alert.show(getCurrentActivity());
    } else if (presentationType == 2) {
      ArrayList<SampleModel> items = new ArrayList<>();
      for (int i = 0;i < values.size();i++) {
        items.add(new SampleModel(values.getString(i)));
      }

      SimpleSearchDialogCompat dialog = new SimpleSearchDialogCompat(getCurrentActivity(), title,
              searchPlaceholder, null, items,
              new SearchResultListener<SampleModel>() {
                @Override
                public void onSelected(BaseSearchDialogCompat dialog,
                                       SampleModel item, int position) {

                  callback.invoke(item.getTitle());
                  dialog.dismiss();
                }
              });


      dialog.show();
      dialog.getSearchBox().setTypeface(Typeface.SERIF);
    }
  }

  private static AlertActionStyle getActionStyle(int actionStyle){
    switch (actionStyle){
      case 0:
        return AlertActionStyle.DEFAULT;
      case 1:
        return AlertActionStyle.NEGATIVE;
      case 2:
        return AlertActionStyle.POSITIVE;
        default:
          return AlertActionStyle.DEFAULT;
    }
  }

  private class SampleModel implements Searchable {
    private String mTitle;

    public SampleModel(String title) {
      mTitle = title;
    }

    @Override
    public String getTitle() {
      return mTitle;
    }

    public SampleModel setTitle(String title) {
      mTitle = title;
      return this;
    }
  }
}