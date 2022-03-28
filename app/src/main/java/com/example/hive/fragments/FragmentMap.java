package com.example.hive;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.hive.models.Desk;
import com.example.hive.services.DeskService;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class FragmentMap extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment

        return inflater.inflate(R.layout.fragment_map, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        Resources res=getResources();
        Bitmap mBitmap = BitmapFactory.decodeResource(res, R.drawable.theofficemap);
        BitmapDrawable bDrawable = new BitmapDrawable(res, mBitmap);
        final ImageView ImageView_BitmapView = view.findViewById(R.id.img);
        //get the size of the image and  the screen
        int bitmapWidth = bDrawable.getIntrinsicWidth();
        int bitmapHeight = bDrawable.getIntrinsicHeight();
        int screenWidth = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

        // set maximum scroll amount (based on center of image)
        int maxX = (bitmapWidth / 2) - (screenWidth / 2);
        int maxY = (bitmapHeight / 2) - (screenHeight / 2);

        // set scroll limits
        final int maxLeft = (maxX * -1);
        final int maxRight = maxX;
        final int maxTop = (maxY * -1);
        final int maxBottom = maxY;

        //Set up user buttons and the user map overlay
        RelativeLayout mapRelativeLayout = view.findViewById(R.id.usermap);
        FrameLayout.LayoutParams mapLayoutParams1 = (FrameLayout.LayoutParams) mapRelativeLayout.getLayoutParams();
        mapLayoutParams1.width = bitmapWidth;
        mapLayoutParams1.height = bitmapHeight;

        // https://stackoverflow.com/questions/18655940/linearlayoutlayoutparams-cannot-be-cast-to-android-widget-framelayoutlayoutpar
        mapRelativeLayout.setLayoutParams(mapLayoutParams1);

        //Get Desk
//        Desk testDesk1 = DeskService.getDeskById(1, );

        // test button
        Button b1 = new Button(mapRelativeLayout.getContext());
        b1.setAlpha(1.0F);
        b1.setText("BtnTest");

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.rightMargin = 0;
        buttonParams.topMargin = 0;
        String location = "(" + b1.getX() + "," + b1.getY() + ")";
        b1.setText(location);
        mapRelativeLayout.addView(b1, buttonParams);


        ImageView_BitmapView.setOnTouchListener(new View.OnTouchListener()
        {
            float downX, downY;
            int totalX, totalY;
            int scrollByX, scrollByY;
            public boolean onTouch(View view, MotionEvent event)
            {
                float currentX, currentY;
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        currentX = event.getX();
                        currentY = event.getY();
                        scrollByX = (int)(downX - currentX);
                        scrollByY = (int)(downY - currentY);

                        // scrolling to left side of image (pic moving to the right)
                        if (currentX > downX)
                        {
                            if (totalX == maxLeft)
                            {
                                scrollByX = 0;
                            }
                            if (totalX > maxLeft)
                            {
                                totalX = totalX + scrollByX;
                            }
                            if (totalX < maxLeft)
                            {
                                scrollByX = maxLeft - (totalX - scrollByX);
                                totalX = maxLeft;
                            }
                        }

                        // scrolling to right side of image (pic moving to the left)
                        if (currentX < downX)
                        {
                            if (totalX == maxRight)
                            {
                                scrollByX = 0;
                            }
                            if (totalX < maxRight)
                            {
                                totalX = totalX + scrollByX;
                            }
                            if (totalX > maxRight)
                            {
                                scrollByX = maxRight - (totalX - scrollByX);
                                totalX = maxRight;
                            }
                        }

                        // scrolling to top of image (pic moving to the bottom)
                        if (currentY > downY)
                        {
                            if (totalY == maxTop)
                            {
                                scrollByY = 0;
                            }
                            if (totalY > maxTop)
                            {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY < maxTop)
                            {
                                scrollByY = maxTop - (totalY - scrollByY);
                                totalY = maxTop;
                            }
                        }

                        // scrolling to bottom of image (pic moving to the top)
                        if (currentY < downY)
                        {
                            if (totalY == maxBottom)
                            {
                                scrollByY = 0;
                            }
                            if (totalY < maxBottom)
                            {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY > maxBottom)
                            {
                                scrollByY = maxBottom - (totalY - scrollByY);
                                totalY = maxBottom;
                            }
                        }

                        mapRelativeLayout.scrollBy(scrollByX, scrollByY);
                        ImageView_BitmapView.scrollBy(scrollByX, scrollByY);
                        downX = currentX;
                        downY = currentY;
                        break;

                }

                return true;
            }
        });
    }
}
