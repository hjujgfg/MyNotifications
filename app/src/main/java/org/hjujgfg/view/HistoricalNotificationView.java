package org.hjujgfg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.hjujgfg.MainActivity;
import org.hjujgfg.R;
import org.hjujgfg.history.AsyncForgetNotification;
import org.hjujgfg.history.DoNotForgetNotification;

import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

public class HistoricalNotificationView extends LinearLayout {

    private final static DateTimeFormatter FORMATTER = ofPattern("hh:mm:ss DD.MM.YYYY");

    private DoNotForgetNotification notification;
    private View view;

    private Button forget, remember;

    public HistoricalNotificationView(Context context) {
        super(context);
    }

    public HistoricalNotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoricalNotificationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HistoricalNotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HistoricalNotificationView(MainActivity context,
                                      ViewGroup parent,
                                      DoNotForgetNotification notification) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.history_item, null);
        }
        parent.addView(view);
        //view = inflate(context, R.layout.history_item, parent);
        this.notification = notification;

        TextView contentView = view.findViewById(R.id.notification_content);
        contentView.setText(notification.content);
        TextView dateView = view.findViewById(R.id.notification_date);
        dateView.setText(notification.date.format(FORMATTER));

        forget = view.findViewById(R.id.forget_btn);
        forget.setOnClickListener(v ->
                new AsyncForgetNotification(context.getDb(), context::triggerReloadHistory)
                        .execute(notification));
        remember = view.findViewById(R.id.reremember_btn);
        remember.setOnClickListener(v -> context.startNotification(notification.content));
    }

}
