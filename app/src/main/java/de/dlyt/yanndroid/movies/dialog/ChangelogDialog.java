package de.dlyt.yanndroid.movies.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.dlyt.yanndroid.movies.R;

public class ChangelogDialog extends BottomSheetDialogFragment {


    public static ChangelogDialog newInstance() {
        ChangelogDialog fragment = new ChangelogDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.changelog_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        String changlog = "\n" +
                "Soon\n" +
                "- redesign trailer view\n" +
                "- Grid or list layout\n" +
                "- change some icons\n" +
                "- (drawer instead of 3 dots) ?\n" +
                "\n" +
                "1.6.4\n" +
                "- favorites now working\n" +
                "- change language without restart\n" +
                "- more OneUI design\n" +
                "- added changelog\n" +
                "\n" +
                "1.6.3\n" +
                "- added genre\n" +
                "- search and filter now working\n" +
                "- added some settings\n" +
                "- the usual like bug fix\n" +
                "\n" +
                "1.6.2\n" +
                "- install apk working\n" +
                "- improved language\n" +
                "- improved tmdb\n" +
                "- added trailer View\n" +
                "- started logging\n";




        TextView changelog_text = dialog.findViewById(R.id.changelog_text);
        changelog_text.setText(changlog);

    }
}