package com.example.offline;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.example.offline.backend.rewards.Reward;
import com.example.offline.backend.rewards.RewardManager;

import java.util.ArrayList;

public class StickersAdapter extends BaseAdapter {

    private final ArrayList rewards;
    LayoutInflater inflater;
    private final int completedGoals;
    private RewardManager rewardManager;
    private Context mContext;

    public StickersAdapter(Context context, ArrayList rewards, int completedGoals) {
        this.rewards = rewards;
        this.completedGoals = completedGoals;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rewardManager = RewardManager.getInstance(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return rewards.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_rewards_content, null);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.img);
        if (((Reward) rewards.get(position)).isUnlocked()) {
            image.setImageResource(R.drawable.ic_giftbox);
            setUnlocked(image);
            if (((Reward) rewards.get(position)).isOpened()) {
                image.setImageResource(((Reward) rewards.get(position)).getResourceId());
                setUnlocked(image);
            }
        } else {
            image.setImageResource(R.drawable.ic_giftbox);
            setLocked(image);
        }

        TextView progressText = (TextView) convertView.findViewById(R.id.stickerProgress);
        String accomplishedGoal = "";
        if (completedGoals >= ((Reward) rewards.get(position)).getGoalCount()) {
            accomplishedGoal = ((Reward) rewards.get(position)).getGoalCount() + "/" + ((Reward) rewards.get(position)).getGoalCount();
        } else {
            accomplishedGoal = completedGoals + "/" + ((Reward) rewards.get(position)).getGoalCount();
        }

        progressText.setText(accomplishedGoal);

        // open an unlocked reward when clicked
        convertView.setOnClickListener(view -> {
            if (((Reward) rewards.get(position)).isOpened()) {
                AlertDialog.Builder unwrapPresentDialogBuilder = new AlertDialog.Builder(mContext);

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v_sticker_details_dialog = layoutInflater.inflate(R.layout.dialog_sticker_details, null);
                unwrapPresentDialogBuilder.setView(v_sticker_details_dialog);
                AlertDialog unwrapPresentDialog = unwrapPresentDialogBuilder.create();
                unwrapPresentDialog.show();

                ImageView iv_sticker_image = (ImageView) v_sticker_details_dialog.findViewById(R.id.iv_revealed_sticker);
                TextView tv_sticker_name = (TextView) v_sticker_details_dialog.findViewById(R.id.tv_revealed_sticker_name);
                TextView tv_sticker_meme = (TextView) v_sticker_details_dialog.findViewById(R.id.tv_revealed_sticker_meme);

                iv_sticker_image.setImageResource(((Reward) rewards.get(position)).getResourceId());
                tv_sticker_name.setText(((Reward) rewards.get(position)).getName());
                tv_sticker_meme.setText(((Reward) rewards.get(position)).getDescription());
            } else if (((Reward) rewards.get(position)).isUnlocked()) {
                buildPresentDialogs(((Reward) rewards.get(position)), image);
            }
        });

        return convertView;
    }

    public static void setLocked(ImageView v) {
        // Function to make ImageView grayscale.
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setImageAlpha(255);
    }

    public static void setUnlocked(ImageView v) {
        // Function to get it back to normal.
        v.setColorFilter(null);
        v.setImageAlpha(255);
    }

    private void buildPresentDialogs(Reward reward, ImageView image) {

        // build the unwrap reward dialog
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder unwrapPresentDialogBuilder = new AlertDialog.Builder(mContext);
        View viewUnwrapPresentDialog = layoutInflater.inflate(R.layout.dialog_unwrap_present, null);
        LottieAnimationView avDialogPresent = (LottieAnimationView) viewUnwrapPresentDialog.findViewById(R.id.av_present_to_unwrap);

        unwrapPresentDialogBuilder.setView(viewUnwrapPresentDialog);
        AlertDialog unwrapPresentDialog = unwrapPresentDialogBuilder.create();
        unwrapPresentDialog.show();

        // open dialog with revealed reward when the present animation is pressed
        avDialogPresent.setOnClickListener(a -> {

            unwrapPresentDialog.dismiss();

            // build the reveal reward dialog
            AlertDialog.Builder revealPresentDialogBuilder = new AlertDialog.Builder(mContext);
            View viewRevealPresentDialog = layoutInflater.inflate(R.layout.dialog_reveal_present, null);

            // find text views and image view which describe the reward
            ImageView ivStickerImage = (ImageView) viewRevealPresentDialog.findViewById(R.id.iv_revealed_sticker);
            TextView tvStickerName = (TextView) viewRevealPresentDialog.findViewById(R.id.tv_revealed_sticker_name);
            TextView tvStickerDescription = (TextView) viewRevealPresentDialog.findViewById(R.id.tv_revealed_sticker_meme);

            // set the reward image, name and description values dynamically based on the earned reward
            ivStickerImage.setImageResource(reward.getResourceId());
            tvStickerName.setText(reward.getName());
            tvStickerDescription.setText(reward.getDescription());

            Button btnClaimSticker = (Button) viewRevealPresentDialog.findViewById(R.id.btn_claim_sticker);

            revealPresentDialogBuilder.setView(viewRevealPresentDialog);
            AlertDialog revealPresentDialog = revealPresentDialogBuilder.create();
            revealPresentDialog.show();

            btnClaimSticker.setOnClickListener(b -> {

                revealPresentDialog.dismiss();
                rewardManager.openReward(reward.getId());
                image.setImageResource(reward.getResourceId());
                setUnlocked(image);
            });
        });
    }

}

