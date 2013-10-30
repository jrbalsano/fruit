package fruit.g4;

public class Vectors {
  public static float dot(float[] a, float[] b){
    float val = 0;
    for (int i = 0 ; i < a.length; i++){
      val += a[i] * b[i];
    }
    return val;
  }
  public static float[] castToFloatArray(int[] intArr){
    float[] tmp = new float[intArr.length];
    for (int i = 0 ; i < intArr.length; i++) tmp[i] = (float) intArr[i];
    return tmp;
  }
  public static float sum(float[] arr){
    float sum = 0;
    for (int i = 0 ; i < arr.length; i++){
      sum += arr[i];
    }
    return sum;
  }

  public static float[] minus(float[] a, float[] b){
    float[] res = new float[a.length];
    for (int i = 0 ; i < a.length; i++){
      res[i] = a[i] - b[i];
    }
    return res;
  }

  public static float[] normalize(float[] arr){
    float sum = sum(arr);
    for (int i = 0 ; i < arr.length; i++){
      arr[i] = arr[i] / sum;
    }
    return arr;
  }

  public static float maxIndex(float[] arr){
    float max = 0;
    float maxVal = Float.MIN_VALUE;
    for (int i = 0 ; i < arr.length; i++){
      if (arr[i] > maxVal) max = i;
    }
    return max;
  }

  public static float[] scale(float[] arr, float factor){
    for (int i = 0 ; i < arr.length; i++){
      arr[i] = arr[i] * factor;
    }
    return arr;
  }
}
