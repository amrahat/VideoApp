package net.optimusbs.videoapp.UtilityClasses;

/**
 * Created by AMRahat on 4/4/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import net.optimusbs.videoapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagLayoutContainer extends ViewGroup {
    private List<View> mNoGoneChildren;
    private Map<View, ChildLayoutMsg> mChildrenMsg;
    private int mLinePadding;
    private int minWidth, minHeight;

    OnTagClickListener onTagClickListener;

    public TagLayoutContainer(Context context) {
        this(context, null);
    }

    public TagLayoutContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagLayoutContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNoGoneChildren = new ArrayList<>();
        mChildrenMsg = new HashMap<>();
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.TagLayoutContainer);
        mLinePadding = dip2px(t.getInt(R.styleable.TagLayoutContainer_line_padding, 0));
        t.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        minWidth = getPaddingLeft() + getPaddingRight();
        minHeight = getPaddingTop() + getPaddingBottom();
        int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                int defSize = getPaddingLeft() + layoutParams.leftMargin
                        + child.getMeasuredWidth() + layoutParams.rightMargin + getPaddingRight();
                if (defSize > getMeasuredWidth()) {
                    defSize = getMeasuredWidth() - layoutParams.leftMargin
                            - layoutParams.rightMargin - getPaddingLeft() - getPaddingRight();
                    int widthSpec = MeasureSpec.makeMeasureSpec(defSize, MeasureSpec.AT_MOST);

                    int heightSpec = getChildMeasureSpec(heightMeasureSpec,
                            getPaddingTop() + getPaddingBottom() + layoutParams.topMargin
                                    + layoutParams.bottomMargin, layoutParams.height);
                    child.measure(widthSpec, heightSpec);
                }
                if (!mNoGoneChildren.contains(child))
                    mNoGoneChildren.add(child);
            }
        }
        writeViewMsg();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY)
            setMeasuredDimension(minWidth, minHeight);
        else if (widthMode != MeasureSpec.EXACTLY)
            setMeasuredDimension(minWidth, getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        else if (heightMode != MeasureSpec.EXACTLY)
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), minHeight);
    }

    private void writeViewMsg() {
        int lineHeight = 0;
        int lineHeightSum = 0;
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        int freeWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        boolean isFirst = true;
        int tmpWidth = 0;

        for (int i = 0; i < mNoGoneChildren.size(); i++) {
            View view = mNoGoneChildren.get(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();

            int childWidth = layoutParams.leftMargin + view.getMeasuredWidth() +
                    layoutParams.rightMargin;
            if (childWidth > freeWidth) {
                isFirst = true;
                lineHeightSum += lineHeight;
                lineHeight = 0;
                freeWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            }

            if (isFirst) {
                left = getPaddingLeft() + layoutParams.leftMargin;
                isFirst = false;
                if (tmpWidth > minWidth)
                    minWidth = tmpWidth;
                tmpWidth = childWidth;
            } else {
                View prView = mNoGoneChildren.get(i - 1);
                MarginLayoutParams ll = (MarginLayoutParams) prView.getLayoutParams();
                left += prView.getMeasuredWidth() + ll.rightMargin + layoutParams.leftMargin;
                tmpWidth += childWidth;
            }
            top = getPaddingTop() + lineHeightSum + mLinePadding + layoutParams.topMargin;
            right = left + view.getMeasuredWidth();
            bottom = top + view.getMeasuredHeight();
            int tmpHeight = mLinePadding * 2
                    + layoutParams.topMargin
                    + view.getMeasuredHeight()
                    + layoutParams.bottomMargin;
            if (tmpHeight > lineHeight)
                lineHeight = tmpHeight;
            freeWidth -= childWidth;

            if (mChildrenMsg.containsKey(view))
                mChildrenMsg.remove(view);
            mChildrenMsg.put(view, new ChildLayoutMsg(left, top, right, bottom));
        }
        lineHeightSum += lineHeight;
        minHeight += lineHeightSum;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Set<View> set = mChildrenMsg.keySet();
        for (View child : set) {
            ChildLayoutMsg msg = mChildrenMsg.get(child);
            child.layout(msg.left, msg.top, msg.right, msg.bottom);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static final class ChildLayoutMsg {
        ChildLayoutMsg(int l, int t, int r, int b) {
            left = l;
            top = t;
            right = r;
            bottom = b;
        }

        int left;
        int right;
        int top;
        int bottom;
    }

    public void setLinePadding(int paddingSize) {
        mLinePadding = dip2px(paddingSize);
    }


    public void addTags(Activity activity, ArrayList<String> list) {
        final int[] height = {0};
        for (int i = 0; i < list.size(); i++) {
            final String tagString = list.get(i);
            final View view = activity.getLayoutInflater().inflate(R.layout.tag_view, null);

            final TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(tagString);
            textView.setTextSize(10);
            // textView.setId(i);
            MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int) dip2px(2);
            lp.topMargin = (int) dip2px(2);
            //   lp.leftMargin = (int) dip2px(5);
            lp.rightMargin = (int) dip2px(6);
            view.setActivated(false);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTagClickListener != null)
                        onTagClickListener.onTagClick(finalI);


                }
            });

            /*view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Log.d("asdfsaf", "onGlobalLayout: "+view.getHeight());
                    height[0] = view.getHeight();
                    minHeight = height[0];
                    ((LayoutParams)getLayoutParams()).height = (view.getHeight() *2)+(int) dip2px(9);

                }
            });*/







            addView(view, i, lp);

        }
    }


    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;

    }


    public interface OnTagClickListener {
        void onTagClick(int position);
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);

    }
}
