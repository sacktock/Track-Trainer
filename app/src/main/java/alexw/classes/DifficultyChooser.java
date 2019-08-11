package alexw.classes;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by alexw on 9/9/2017.
 */

public class DifficultyChooser {

    private ImageView easy;
    private ImageView good;
    private ImageView challenging;
    private ImageView hard;
    private Completed_Training.Difficulty difficulty;

    public Completed_Training.Difficulty getDifficulty(){return  difficulty;}

    public DifficultyChooser(ImageView easy, ImageView good, ImageView challenging, ImageView hard){
        //Constructor
        this.easy = easy;
        this.good = good;
        this.challenging = challenging;
        this.hard = hard;
    }

    public void setDifficulty(Completed_Training.Difficulty difficulty){
        //Programmatically set difficulty of interface
        this.difficulty = difficulty;
        if (difficulty == Completed_Training.Difficulty.Easy){
            easy.callOnClick();
        }
        if (difficulty == Completed_Training.Difficulty.Good){
            good.callOnClick();
        }
        if (difficulty == Completed_Training.Difficulty.Challenging){
            challenging.callOnClick();
        }
        if (difficulty == Completed_Training.Difficulty.Hard){
            hard.callOnClick();
        }
    }

    public void setUpImageView(){
        //Method sets up interface
        removeAllAlpha();
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllAlpha();
                easy.setAlpha(1f);
                difficulty = Completed_Training.Difficulty.Easy;
            }
        });
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllAlpha();
                good.setAlpha(1f);
                difficulty = Completed_Training.Difficulty.Good;
            }
        });
        challenging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllAlpha();
                challenging.setAlpha(1f);
                difficulty = Completed_Training.Difficulty.Challenging;
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllAlpha();
                hard.setAlpha(1f);
                difficulty = Completed_Training.Difficulty.Hard;
            }
        });

        easy.callOnClick();
        //Easy is the default selected difficulty

        //Only one difficulty can be selected
        //When ImageView is clicked then it is made opaque
        //the rest of the ImageViews are made 20% visible
    }

    private void removeAllAlpha(){
        //Sets all ImageViews to 20% visibility
        easy.setAlpha(0.2f);
        good.setAlpha(0.2f);
        challenging.setAlpha(0.2f);
        hard.setAlpha(0.2f);
    }
}
