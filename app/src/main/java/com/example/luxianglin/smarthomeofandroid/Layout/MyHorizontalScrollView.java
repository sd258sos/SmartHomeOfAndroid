package com.example.luxianglin.smarthomeofandroid.Layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luxianglin.smarthomeofandroid.R;


public class MyHorizontalScrollView extends HorizontalScrollView
{

	/**
	 * 关联的ViewPager
	 */
	private ViewPager mViewPager;

	/**
	 * HorizontalScrollView的子容器布局
	 */
	private LinearLayout mTabContainer;

	/**
	 * github上找的自定义drawable，直接拿来用吧。 https://github.com/devunwired/textdrawable
	 */
	private TextDrawable[]			mTextDrawable	= new TextDrawable[3];

	/**
	 * 利用clipRect高亮选中的区域
	 * 这个Drawable设为透明
	 */
	private Drawable mDrawable;

	private MyOnPageChangeListener	mOnPageChangeListener;

	private int						currentPosition;

	private float					currentPositionOffset;

	private int						lastScrollX		= 0;

	// 滑动的时候边界的阀值，改成0你可以看看效果你就明白是干啥的了，这个值要写大一些
	private int						offset			= 30;

	private int						count			= 0;

	/**
	 * 选中的区域
	 */
	private Rect mRect;

	private LayoutInflater mLayoutInflater;

	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public MyHorizontalScrollView(Context context)
	{
		this(context, null, 0);
	}

	/**
	 * 初始化
	 */
	private void init()
	{
		mRect = new Rect();

		mTabContainer = new LinearLayout(getContext());
		mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
		mTabContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mTabContainer);

		mOnPageChangeListener = new MyOnPageChangeListener();

		mDrawable = getResources().getDrawable(R.drawable.bg_selected);

		for (int i = 0; i < 3; i++)
		{
			mTextDrawable[i] = new TextDrawable(getContext());
		}

		mLayoutInflater = LayoutInflater.from(getContext());
	}

	private void calculateRect()
	{
		View currentTab = mTabContainer.getChildAt(currentPosition);

		View currentTextView = currentTab.findViewById(R.id.tv_item);
		float left = (float) (currentTab.getLeft() + currentTextView.getLeft());
		float width = ((float) currentTextView.getWidth()) + left;

		// 如果滑动了 并且 下表要小于最大值 就是倒数第二个
		// 由于viewpager向右话是下标为当前页，向左滑时为当前页减1
		// 计算位置时注意 getLeft是相对于父布局的
		if (currentPositionOffset > 0.0f && currentPosition < count - 1)
		{
			View nextTab = mTabContainer.getChildAt(currentPosition + 1);
			View nextTextView = nextTab.findViewById(R.id.tv_item);

			float nextLeft = (float) (nextTab.getLeft() + nextTextView.getLeft());

			left = left + (nextLeft - left) * currentPositionOffset;
			width = width + (nextLeft + nextTextView.getWidth() - width) * currentPositionOffset;
		}

		// 这里可以画画图理解一下，我也是拿个本子哗啦哗啦，再试试能调出来
		int newLeft = (int) (currentTab.getPaddingLeft() + left);
		int newRight = (int) (width + currentTab.getPaddingLeft());
		int newTop = getPaddingTop() + currentTab.getTop() + currentTextView.getTop();
		int newBottom = getPaddingTop() + currentTab.getTop() + currentTextView.getHeight() + currentTextView.getTop();

		mRect.set(newLeft, newTop, newRight, newBottom);
	}

	// 计算滑动
	private void scrollToChild(int position)
	{
		if (count == 0)
		{
			return;
		}

		calculateRect();
		int newScrollX = lastScrollX;
		// 此时判断向左滑动
		// 如果边界不够了，就需要腾出offset的边界值
		// 就是判断是不是滑动到左边界了
		if (mRect.left < getScrollX() + offset)
		{
			newScrollX = mRect.left - offset;

			// 判断向右滑动
			// 道理同上边的左边界判断，这个是判断右边界的
		}
		else if (mRect.right > getScrollX() + getWidth() - offset)
		{
			newScrollX = mRect.right - getWidth() + offset;
		}

		// 有变化了就更新吧
		if (newScrollX != lastScrollX)
		{
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		calculateRect();
		mDrawable.setBounds(mRect);
		mDrawable.draw(canvas);

		for (int i = 0; i < count; i++)
		{
			if (i >= currentPosition - 1 && i <= currentPosition + 1)
			{
				View tabContainer = mTabContainer.getChildAt(i);
				TextView tabTextView = (TextView) tabContainer.findViewById(R.id.tv_item);
				canvas.clipRect(mRect);
				TextDrawable textDrawable = mTextDrawable[i - currentPosition + 1];
				textDrawable.setText(tabTextView.getText());
				textDrawable.setTextSize(0, tabTextView.getTextSize());
				textDrawable.setTextColor(getResources().getColor(R.color.highLight));

				int left = tabContainer.getLeft() + tabTextView.getLeft() + tabTextView.getWidth() / 2 - textDrawable.getIntrinsicWidth() / 2 + getPaddingLeft();
				// 实际显示出来有1两个像素差，字体放大了才明显，20sp一下吧都是可以接受的
				float top = (getHeight() - textDrawable.getIntrinsicHeight()) / 2 + getPaddingTop();
				int right = textDrawable.getIntrinsicWidth() + left;
				float bottom = textDrawable.getIntrinsicHeight() + top;
				int rTop = Math.round(top + 1.5f);
				int rBottom = Math.round(bottom + 1.5f);
				textDrawable.setBounds(left, rTop, right, rBottom);
				textDrawable.draw(canvas);

			}
		}
	}

	/**
	 * 设置关联ViewPager
	 * 
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager)
	{
		this.mViewPager = viewPager;
//		mViewPager.addOnPageChangeListener(mOnPageChangeListener);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		MainActivity.MyPagerAdapter adapter = (MainActivity.MyPagerAdapter) mViewPager.getAdapter();
		count = adapter.getCount();
		int i = 0;
		for (; i < count; i++)
		{
			CharSequence title = adapter.getPageTitle(i);
			final int position = i;
			ViewGroup tab = (ViewGroup) mLayoutInflater.inflate(R.layout.item_tab, this, false);
			TextView tabText = (TextView) tab.findViewById(R.id.tv_item);
			tabText.setText(title);
			tabText.setGravity(Gravity.CENTER);
			tabText.setSingleLine();
			tabText.setFocusable(true);
//			tabText.setBackgroundColor(Color.YELLOW);
			tab.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mViewPager.setCurrentItem(position);
				}
			});

			mTabContainer.addView(tab, position, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		}

	}

	private class MyOnPageChangeListener implements OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int state)
		{

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{
			currentPosition = position;
			currentPositionOffset = positionOffset;

			scrollToChild(position);

			invalidate();
		}

		@Override
		public void onPageSelected(int position)
		{

		}

	}

}
