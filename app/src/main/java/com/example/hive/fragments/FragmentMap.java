package com.example.hive.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Layout;
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

import com.example.hive.R;
import com.example.hive.services.Response;

import java.util.Map;
import java.util.Objects;

public class FragmentMap extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    public void makeButton(int x, int y, String deskID, RelativeLayout relLayout, int mapWidth, int mapHeight) {
        //puts a button the specified relative layout
        Button b1 = new Button(relLayout.getContext());
        b1.setAlpha(1.0F);
        b1.setText("Desk" + deskID);
        //Set the button params, the position of it in its parent relative layout
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.leftMargin = (int) Math.round(x/100.0 * mapWidth); //map width is the width of the image, in pixels
        buttonParams.topMargin = (int) Math.round(y/100.0 * mapWidth);
        String location = "(" + x + "," + y + ")";
        b1.setText(location);
        relLayout.addView(b1, buttonParams); //add the button
    }

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
        @SuppressLint("UseRequireInsteadOfGet")
        int screenWidth = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = (int) (getActivity().getWindowManager().getDefaultDisplay().getHeight() * 0.5);

        // set maximum scroll amount (based on center of image)
        int maxX = (bitmapWidth / 2) - (screenWidth / 2);
        int maxY = (bitmapHeight / 2) - (screenHeight / 2);

        // set scroll limits
        final int maxLeft = -maxX;
        final int maxRight = maxX;
        final int maxTop = -maxY;
        final int maxBottom = maxY;

        //Set up user buttons and the user map overlay
        RelativeLayout mapRelativeLayout = view.findViewById(R.id.usermap);
        FrameLayout.LayoutParams mapLayoutParams1 = (FrameLayout.LayoutParams) mapRelativeLayout.getLayoutParams();
        mapLayoutParams1.width = bitmapWidth;
        mapLayoutParams1.height = bitmapHeight;

        // https://stackoverflow.com/questions/18655940/linearlayoutlayoutparams-cannot-be-cast-to-android-widget-framelayoutlayoutpar
        mapRelativeLayout.setLayoutParams(mapLayoutParams1);

        //Get Desk
        // onSuccess, will use makeButton to add in the user
        String deskID2 = "2";
        DeskService.getDeskById(deskID2, new Response() {
            @Override
            public void onSuccess(Object data) {
                Map data2= (Map) data;
                System.out.println(data2.toString());
                int button_x = (int) (long) data2.get("location_x"); //idk why but apparently firebase gives long
                int button_y = (int) (long) data2.get("location_y");
                makeButton(button_x, button_y, deskID2, mapRelativeLayout, bitmapWidth, bitmapHeight);
            }
            @Override
            public void onFailure() {
                System.out.println("Failed to load: " + deskID2 );
            }
        });

        // test button


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
                        else if (currentX < downX)
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
                        else if (currentY < downY)
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