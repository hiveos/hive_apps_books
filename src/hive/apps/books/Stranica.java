package hive.apps.books;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class Stranica extends ImageView {

	public static Paint boja;
	public static mojaPutanja putanja;
	private Paint krugBoja;
	private Path krugPutanja;
	public static ArrayList<mojaPutanja> paths = new ArrayList<mojaPutanja>();
	public static Bitmap MyBitmap;
	public static Canvas mCanvas;
	private static final int INVALID_POINTER_ID = -1;
	Rect clipBounds;
	private float mPozicijaX;
	private float mPozicijaY;
	private float mZadnjiDodirX;
	private float mZadnjiDodirY;
	private float mZadnjaGesturaX;
	private float mZadnjaGesturaY;
	private int mActivePointerId = INVALID_POINTER_ID;

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		MyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(MyBitmap);
	}

	public Stranica(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		inicijalizacija(context);
		postaviKist();
	}

	public Stranica(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		inicijalizacija(context);
		postaviKist();
	}

	public Stranica(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		inicijalizacija(context);
		postaviKist();
	}

	private void inicijalizacija(Context k) {
		boja = new Paint();
		krugBoja = new Paint();
		krugPutanja = new Path();
	}

	private void postaviKist() {

		// ///////// Postavljanje kista //////////
		boja.setAntiAlias(true);
		boja.setColor(Color.BLUE);
		// Da boja bude kist:
		boja.setStyle(Paint.Style.STROKE);
		// Da kist bude okruglog oblika:
		boja.setStrokeJoin(Paint.Join.ROUND);
		// Debljina kista
		boja.setStrokeWidth(2f);
		putanja = new mojaPutanja(new Paint(boja));
		paths.add(putanja);

		// ///////// Postavljanje kruga oko kista //////////
		krugBoja.setAntiAlias(true);
		krugBoja.setColor(Color.CYAN);
		krugBoja.setStyle(Paint.Style.STROKE);
		krugBoja.setStrokeJoin(Paint.Join.MITER);
		krugBoja.setStrokeWidth(4f);
	}

	protected void onDraw(Canvas canvas) {
		canvas.save();
		// mCanvas.save();

		canvas.translate(mPozicijaX, mPozicijaY);
		// mCanvas.translate(mPozicijaX, mPozicijaY);

		if (mScaleDetector.isInProgress()) {
			canvas.scale(mScaleFactor, mScaleFactor,
					mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
			// mCanvas.scale(mScaleFactor, mScaleFactor,
			// mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
		} else {
			canvas.scale(mScaleFactor, mScaleFactor, mZadnjaGesturaX,
					mZadnjaGesturaY);
			// mCanvas.scale(mScaleFactor, mScaleFactor,
			// mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
		}
		super.onDraw(canvas);
		if (!Glavna.LoadaniDrawing.isRecycled()) {
			canvas.drawBitmap(Glavna.LoadaniDrawing, 0, 0, null);
			mCanvas.drawBitmap(Glavna.LoadaniDrawing, 0, 0, null);
		}
		for (mojaPutanja p : paths) {
			canvas.drawPath(p, p.bojaPutanje);
			mCanvas.drawPath(p, p.bojaPutanje);
			canvas.drawPath(krugPutanja, krugBoja);
		}
		clipBounds = canvas.getClipBounds();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if (PageScroller.drawing) {
			float tackaX = e.getX() / mScaleFactor + clipBounds.left;
			float tackaY = e.getY() / mScaleFactor + clipBounds.top;

			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				putanja.moveTo(tackaX, tackaY);
				return true;

			case MotionEvent.ACTION_MOVE:

				putanja.lineTo(tackaX, tackaY);
				krugPutanja.reset();
				krugPutanja.addCircle(tackaX, tackaY, 25, Path.Direction.CW);
				break;

			case MotionEvent.ACTION_UP:
				putanja = new mojaPutanja(new Paint(boja));
				paths.add(putanja);
				krugPutanja.reset();
				break;

			default:
				return false;
			}

			postInvalidate();
			return true;
		} else {
			mScaleDetector.onTouchEvent(e);

			final int action = e.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				if (!mScaleDetector.isInProgress()) {
					final float x = e.getX();
					final float y = e.getY();

					mZadnjiDodirX = x;
					mZadnjiDodirY = y;
					mActivePointerId = e.getPointerId(0);
				}
				break;
			}
			case MotionEvent.ACTION_POINTER_1_DOWN: {
				if (mScaleDetector.isInProgress()) {
					final float gx = mScaleDetector.getFocusX();
					final float gy = mScaleDetector.getFocusY();
					mZadnjaGesturaX = gx;
					mZadnjaGesturaY = gy;
				}
				break;
			}
			case MotionEvent.ACTION_MOVE: {

				// Only move if the ScaleGestureDetector isn't processing a
				// gesture.
				if (!mScaleDetector.isInProgress()) {
					final int pointerIndex = e
							.findPointerIndex(mActivePointerId);
					final float x = e.getX(pointerIndex);
					final float y = e.getY(pointerIndex);

					final float dx = x - mZadnjiDodirX;
					final float dy = y - mZadnjiDodirY;

					mPozicijaX += dx;
					mPozicijaY += dy;

					invalidate();

					mZadnjiDodirX = x;
					mZadnjiDodirY = y;
				} else {
					final float gx = mScaleDetector.getFocusX();
					final float gy = mScaleDetector.getFocusY();

					final float gdx = gx - mZadnjaGesturaX;
					final float gdy = gy - mZadnjaGesturaY;

					mPozicijaX += gdx;
					mPozicijaY += gdy;

					invalidate();

					mZadnjaGesturaX = gx;
					mZadnjaGesturaY = gy;
				}

				break;
			}
			case MotionEvent.ACTION_UP: {
				mActivePointerId = INVALID_POINTER_ID;
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				mActivePointerId = INVALID_POINTER_ID;
				break;
			}
			case MotionEvent.ACTION_POINTER_UP: {

				final int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = e.getPointerId(pointerIndex);
				if (pointerId == mActivePointerId) {
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					mZadnjiDodirX = e.getX(newPointerIndex);
					mZadnjiDodirY = e.getY(newPointerIndex);
					mActivePointerId = e.getPointerId(newPointerIndex);
				} else {
					final int tempPointerIndex = e
							.findPointerIndex(mActivePointerId);
					mZadnjiDodirX = e.getX(tempPointerIndex);
					mZadnjiDodirY = e.getY(tempPointerIndex);
				}

				break;
			}
			}

			return true;
		}

	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 10.0f));
			invalidate();
			return true;
		}
	}

}
