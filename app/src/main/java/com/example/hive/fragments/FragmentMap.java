package com.example.hive.fragments;

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

import com.example.hive.models.User;
import com.example.hive.services.DeskService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hive.R;
import com.example.hive.services.Response;
import com.example.hive.services.UserService;
import com.example.hive.utils.Constants;

import java.util.Map;
import java.util.Objects;

public class FragmentMap extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    public void refreshUserButtons(RelativeLayout mapRelativeLayout, int bitmapWidth, int bitmapHeight) {
        //Get Desk
        // onSuccess, will use makeButton to add in the user
        String[] deskList = {"1", "2", "3", "4"};
        for (String deskID : deskList) {
            DeskService.getDeskById(deskID, new Response() {
                @Override
                public void onSuccess(Object data) {
                    Map data2= (Map) data;
                    System.out.println(data2.toString());
                    final User[] user = new User[1];
                    int button_x = (int) (long) data2.get("location_x"); //idk why but apparently firebase gives long
                    int button_y = (int) (long) data2.get("location_y");
                    String currentUser = (String) data2.get("current_user");
                    if ( currentUser != null && !currentUser.isEmpty()) {
                        UserService.getUserById( String.valueOf(data2.get("current_user")),
                                new Response() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        user[0] = User.fromObject(data);
                                        makeButton(button_x, button_y, deskID, mapRelativeLayout, bitmapWidth, bitmapHeight, user[0]);
                                    }

                                    @Override
                                    public void onFailure() {
                                        System.out.println("Failed to get User for desk");
                                    }
                                });
                    } else {
                        //no user, make empty desk
//                        makeButton();
                    }



                }
                @Override
                public void onFailure() {
                    System.out.println("Failed to load: " + deskID );
                }
            });
        }
    }

    public void makeButton(int x, int y, String deskID, RelativeLayout relLayout, int mapWidth, int mapHeight, User user) {
        //puts a button the specified relative layout
        Button b1 = new Button(relLayout.getContext());
        b1.setAlpha(1.0F);
        b1.setText("Desk" + deskID);
        //Set the button params, the position of it in its parent relative layout
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        // set user details
        if (user != null) {
            b1.setText(user.getFirstName());
            UserDetailsBottomSheet bottomSheet = new UserDetailsBottomSheet();
            Bundle args = new Bundle();
            args.putString("id", user.getId());
            args.putString("fullName", user.getFullName());
            args.putString("email", user.getEmail());
            args.putInt("status", user.getStatus().equals(Constants.Status.AVAILABLE) ? 1 : 0);
            bottomSheet.setArguments(args);

            //set profile picture
//            b1.setBackground(this.getResources().getDrawable(R.drawable.user_circle));
//            b1.setText(x + "," + y + "||" + mapWidth + "," + mapHeight);



            b1.setOnClickListener(l -> {
                bottomSheet.show(((AppCompatActivity) b1.getContext()).getSupportFragmentManager(), user.getId());
            });
        }

         //add the button
        // modify to get the actual dp of the button (margin)
        int buttonWidth = 100;
        int buttonHeight =20;
        buttonParams.leftMargin = (int) Math.round((x/1000.0 * mapWidth) - buttonWidth/2); //map width is the width of the image, in pixels
        buttonParams.topMargin = (int) Math.round((y/1000.0 * mapHeight) - buttonHeight/2);

        relLayout.addView(b1, buttonParams);
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
        //get the data for the user buttons and make the buttons
        //test button

        refreshUserButtons(mapRelativeLayout, bitmapWidth, bitmapHeight);
        // test button
        Button b2 = new Button(mapRelativeLayout.getContext());
        b2.setAlpha(1.0F);
        b2.setText("CornerDesk");
        //Set the button params, the position of it in its parent relative layout
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.leftMargin = 0; //map width is the width of the image, in pixels
        buttonParams.topMargin = 0;
        mapRelativeLayout.addView(b2, buttonParams);

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

//        // setup update handler
//        final Handler handler = new Handler();
//        final int delayMs = 2000;
//
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                updateLight();
//                handler.postDelayed(this, delayMs);
//            }
//        }, delayMs);

    }
}