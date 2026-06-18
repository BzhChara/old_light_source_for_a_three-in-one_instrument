package top.jemen.utils;

import java.util.Arrays;
import java.util.List;


import top.jemen.utils.QVMProtect;

/**
 * Jemen write if for find a curve's charecter
 * 
 * @author Jemen Chen
 *
 */
public class Curve {
	/**
	 * 查找极值点
	 * 
	 * @param xArray
	 *            x坐标
	 * @param list
	 *            y坐标
	 * @param steps
	 *            检测的步数
	 * @return 定点的坐标
	 */
	@QVMProtect
	public static int findTop(List<Float> list, int l, int r, int steps) {
		if ( null == list || steps < 1)
			return -1;
		int n = -1, size = list.size()-1;
		if (l < 0)
			l = 0;
		if (r > size-1)
			r = size-1;
		double t = list.get(0);
		if (size <= steps * 2) {
			// for(int i=l;i<=r;i++) {
			// if(yValue[i]<t) {
			// t=yValue[i];
			// n=i;
			// }
			// }
			return -1;
		} else {
			int maxS = 0, maxD = 0;
			for (int i = l + steps; i < r - steps; i++) {
				int s = 0, d = 0;
				// 为支持对非光滑曲线的处理，引入以下变量。
				for (int j = 1; j <= steps; j++) {
					if (list.get(i - j) < list.get(i - j + 1))
						s++;
					if (list.get(i + j) < list.get(i + j - 1))
						d++;
				}
				if (s > steps * 0.85 && d > steps * 0.85) {
					if (s + d > maxS + maxD) {
						n = i;
						maxS = s;
						maxD = d;
					}
				}
			}
		}
		return n;
	}

	
	@QVMProtect
	public static int findInflection(List<Float> yValue, int l, int r, int steps) {
		if ( null == yValue||yValue.size()<3|| steps < 1)
			return -1;
		int n = -1, size = yValue.size();
		if (l < 0)
			l = 0;
		if (r > size)
			r = size;
		double t = yValue.get(0);
		if (size <= steps * 2) {
			return -1;
		}
		int maxS = 0, maxD = 0;
		for (int i = l + steps; i < r - steps; i++) {
			int s = 0, d = 0;
			double a=yValue.get(i-steps),b=yValue.get(i+steps);
			double k=(b-a)/(steps*2);
			
			// 为支持对非光滑曲线的处理，引入以下变量。
			for (int j = 1; j <= steps; j++) {
				if (yValue.get(i - j)-k*(steps-j) > yValue.get(i - j + 1)-k*(steps-j+1))
					s++;
				if (yValue.get(i + j)-k*(steps+j) > yValue.get(i + j - 1)-k*(steps+j-1))
					d++;
			}
			
			if (s > steps * 0.8 && d > steps * 0.8) {
				if (s + d >= maxS + maxD) {
					n = i;
					maxS = s;
					maxD = d;
				}
			}
		}
		return n;
	}
	
	

	/**
	 * 查找极大值点。 寻找峰点从右向左找
	 * 
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 * @param steps
	 *            检测的步数
	 * @return 定点的坐标
	 */
	@QVMProtect
	public static int findTopR(double[] x, double[] y, int l, int r, int steps) {
		if (null == x || null == y || steps < 1)
			return -1;
		int n = -1, size = x.length > y.length ? y.length : x.length;
		if (l < 0)
			l = 0;
		if (r > size)
			r = size;
		double t = y[0];
		if (size <= steps) {
			for (int i = l + 1; i < r; i++) {
				if (y[i] < t) {
					t = y[i];
					n = i;
				}
			}
		} else {
			for (int i = r - steps; i >= l + steps; i--) {
				// 为支持对非光滑曲线的处理，引入以下变量。
				int s = 0, d = 0;
				for (int j = 1; j <= steps; j++) {
					if (y[i - j] > y[i - j + 1])
						s++;
					if (y[i + j] > y[i + j - 1])
						d++;
				}
				if (s > steps * 0.8 && d > steps * 0.8)
					return i;
			}
		}
		return n;
	}

	/**
	 * 查找极小值点,低点，也就是y坐标较大的。
	 * 
	 * @param x
	 * @param y
	 * @param steps
	 * @return
	 */
	@QVMProtect
	public static int findFoot(double[] x, double[] y, int steps) {
		if (null == x || null == y || steps < 1)
			return -1;
		int n = -1, size = x.length > y.length ? y.length : x.length;
		double t = y[0];
		if (size <= steps / 2) {
			for (int i = 1; i < size; i++) {
				if (y[i] > t) {
					t = y[i];
					n = i;
				}
			}
		} else {
			for (int i = steps; i < size - steps; i++) {
				// 为支持对非光滑曲线的处理，引入以下变量。
				int s = 0, d = 0;
				for (int j = 1; j <= steps; j++) {
					if (y[i - j] < y[i - j + 1])
						s++;
					if (y[i + j] < y[i + j - 1])
						d++;
				}
				if (s > steps * 0.8 && d > steps * 0.8)
					return i;
			}
		}
		return n;
	}


	/***
	 * 找到凹弧，凹点
	 * 
	 * @return
	 */
	public static int findCove() {
		return 0;
	}

	/**
	 * 线性回归
	 * 
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 * @param len
	 *            数据长度
	 * @param power
	 *            最高次幂
	 * @return
	 */
	public static double[] regress(double[] x, double[] y, int len, int power) {
		// final WeightedObservedPoints obs = new WeightedObservedPoints();
		// for(int i=0;i<len;i++) {
		// obs.add(x[i],y[i]);
		// }
		// final PolynomialCurveFitter fitter =
		// PolynomialCurveFitter.create(power);//参数为最高此项的次数
		// final double[] coeff = fitter.fit(obs.toList());
		// LogUtil.d("回归方程系数:"+Arrays.toString(coeff));
		// return coeff;
		return new double[] { 0, 0 };
	}

	public double matchGaussian(float[] x, float[] y, int a, int b, int c) {

		return 0;
	}
	
	
	public static int getMinIndex( List<Float> values,double start,double end) {
		return getMinIndex(values, (int)start, (int)end);
	}
	public static int getMinIndex( List<Float> values,int start,int end) {
		if(values.size()<10) {
			return -1;
		}
		if(start<2) {
			start=2;
		}
		if(end>=values.size()) {
			end=values.size()-1;
		}
		if(start<10) {
			start=10;
		}
		int r=start;
		Float t=values.get(start);
		for(int i=start+1;i<=end;i++) {
			if(values.get(i)<t) {
				r=i;
				t=values.get(i);
			}
		}
		return r;
	}

	
	/**
	 * 寻最值点，兼容从左到右以及从右到左
	 * @param values
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getMaxIndex( List<Float> values,double start,double end) {
		return getMaxIndex(values, (int)start, (int)end);
	}
	
	public static int getMaxIndex( List<Float> values,int start,int end) {
		if(values.size()<10) {
			return -1;
		}
		int r=start;
		int dirct=end>start?1:-1;
		if(end>start) {
			if(end>=values.size()-3) {
				end=values.size()-3;
			}
			if(start<2) {
				start=2;
			}
		}else {
			if(start>=values.size()-3) {
				start=values.size()-3;
			}
			if(end<2) {
				end=2;
			}
		}
		
		Float t=values.get(start);
		for(int i=start+dirct;Math.abs(i-end)>1;i+=dirct) {
			if(values.get(i)>t) {
				r=i;
				t=values.get(i);
			}
		}
		return r;
	}
	
	public static int getHumpIndex( List<Float> values,int start,int end) {
		if(values.size()<10) {
			return -1;
		}
		if(end>start) {
			if(end>=values.size()) {
				end=values.size()-1;
			}
			if(start<2) {
				start=2;
			}
		}else {
			if(start>=values.size()) {
				start=values.size()-1;
			}
			if(end<2) {
				end=2;
			}
		}
		
		int dirct=end>start?1:-1;
		float k=(values.get(end)-values.get(start))/(end-start);
		int r=start;
		Float t=0f;
		for(int i=start+dirct;Math.abs(i-end)>1;i+=dirct) {
			float d=values.get(i)-values.get(start)+k*(i-start);
			if(d>t) {
				t=d;
				r=i;
			}
		}
		return r;
	}


	public static int getHumpIndex(List<Float> values, double d, double r) {
		return getHumpIndex(values, (int)d, (int)r);
	}
	
	
	
	
	/**查找极值点 ,
	 * @param xArray	x坐标
	 * @param yValue	y坐标
	 * @param steps	检测的步数
	 * @return	定点的坐标
	 */
	public static int findTop(float[] xArray,float[] yValue,int l,int r,int steps) {
		if(null==xArray||null==yValue||steps<1) return -1;
		int n=-1,size=xArray.length>yValue.length?yValue.length:xArray.length;
		if(l<0)	l=0;
		if(r>size)	r=size;
		double t=yValue[0];
		if(size<=steps*2) {
//			for(int i=l;i<=r;i++) {
//				if(yValue[i]<t) {
//					t=yValue[i];
//					n=i;
//				}
//			}
			return -1;
		}else {
			int maxS = 0,maxD = 0;
			for(int i=l+steps;i<r-steps;i++) {
				int s=0,d=0;
				//为支持对非光滑曲线的处理，引入以下变量。
				for(int j=1;j<=steps;j++) {
					if(yValue[i-j]>yValue[i-j+1])	s++;
					if(yValue[i+j]>yValue[i+j-1])	d++;
				}
				if(s>steps*0.85&&d>steps*0.85) {
					if(s+d>maxS+maxD) {
						n=i;
						maxS=s;
						maxD=d;
					}
				}
			}
		}
		return n;
	}
	
	
	
	
	/**
	 * 查找拐点
	 * 
	 * @param xArray
	 *            x坐标
	 * @param yValue
	 *            y坐标
	 * @param steps
	 *            检测的步数
	 * @return 定点的坐标
	 */
	@QVMProtect
	public static int findInflection(float[] xArray, float[] yValue, int l, int r, int steps) {
		if (null == xArray || null == yValue || steps < 1)
			return -1;
		int n = -1, size = xArray.length > yValue.length ? yValue.length : xArray.length;
		if (l < 0)
			l = 0;
		if (r > size)
			r = size;
		double t = yValue[0];
		if (size <= steps * 2) {
			return -1;
		}
		int maxS = 0, maxD = 0;
		for (int i = l + steps; i < r - steps; i++) {
			int s = 0, d = 0;
			double a=yValue[i-steps],b=yValue[i+steps];
			double k=(b-a)/(steps*2);
			
			// 为支持对非光滑曲线的处理，引入以下变量。
			for (int j = 1; j <= steps; j++) {
				if (yValue[i - j]-k*(steps-j) > yValue[i - j + 1]-k*(steps-j+1))
					s++;
				if (yValue[i + j]-k*(steps+j) > yValue[i + j - 1]-k*(steps+j-1))
					d++;
			}
			
			if (s > steps * 0.8 && d > steps * 0.8) {
				if (s + d >= maxS + maxD) {
					n = i;
					maxS = s;
					maxD = d;
				}
			}
		}
		return n;
	}
	
	public static int bathFoot(List<Float> values,int a,int c,int start,int end){
		if(null==values||values.size()<3||a<0||c<0||start<0||end<0) {
			return -1;
		}
		int r=start;
		if(end==start) {
			return r;
		}
		if(a>c) {
			a^=c;
			c^=a;
			a^=c;
		}
		double k=(values.get(c)-values.get(a))/(c-a);
		int s=end>start?1:-1;
		double dx=Double.MAX_VALUE;
		for(int i=start;Math.abs(end-i)>0;i+=s) {
			double d=values.get(i)-(values.get(a)+k*(i-a));
			if(d<dx) {
				dx=d;
				r=i;
			}
		}
		
		return r;
	}
	
	
}
