
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

public class EmpericalCollaborativeFiltering {
	private static TreeMap<String,TreeMap<String,String>> trainDataMovie;
	private static Hashtable<Integer,TreeMap<String,String>> m2U;
	private static Hashtable<Integer,TreeMap<String,String>> u2M;
	private static Hashtable<Integer,Double> sum;
	private static Hashtable<Integer,Integer> counter;
	private static HashSet<Integer> usersList;
	private static String trainingFileName;
	private static String testingFileName;

	public static void main(String[] args) throws Exception {
		trainingFileName=args[0];
		testingFileName=args[1];

		trainDataMovie=new TreeMap<String, TreeMap<String,String>>();
		System.out.println("Read data started");
		usersList=new HashSet<Integer>();
		readData();
		System.out.println("Read data ended");
		//File fileName = new File("C:/Users/darshan/Desktop/MS/Text Books/Machine Learning/Assignment/Assignment 3/netflix/TestingRatings.txt");
		File fileName = new File(testingFileName);
		BufferedReader x=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String strnew1=x.readLine();
		int count=0;
		double meanError=0,meanSquareError=0;

		while(strnew1!=null){
			String[] splitOp=strnew1.split(",");
			String moviename=splitOp[0];
			String userId=splitOp[1];
			String classVal=splitOp[2];
			//System.out.println(strnew1);
			double vaBar=(double)(sum.get(userId.hashCode()))/(counter.get(userId.hashCode()));
			//System.out.println(vaBar);
			Iterator<Integer> users=usersList.iterator();
			String athUser=userId;
			double wai=0,tempSum=0,waiSum=0;
			count++;
			while(users.hasNext()){
				Integer ithUser=users.next();
				double numSum=0,denom1Sum=0,denom2Sum=0,viBar=0;
				if(!(ithUser==Integer.parseInt((athUser))))
				{
					/*System.out.println("ath"+athUser);
					System.out.println("ith"+ithUser);
					 */
					viBar=(double)(sum.get(ithUser.toString().hashCode()))/(counter.get(ithUser.toString().hashCode()));
					//System.out.println(viBar);
					TreeMap<String, String> athUserMovies=u2M.get(athUser.hashCode());
					Iterator<String> athUserMoviesIter=athUserMovies.keySet().iterator();

					while(athUserMoviesIter.hasNext()){
						String movie=athUserMoviesIter.next();
						if(u2M.get(ithUser.toString().hashCode()).containsKey(movie)==true){
							//System.out.println("Movie is common "+movie);
							double athUserMovieRating=Double.parseDouble(u2M.get(athUser.hashCode()).get(movie));
							double ithUserMovieRating=Double.parseDouble(u2M.get(ithUser.toString().hashCode()).get(movie));
							/*System.out.println(athUserMovieRating);
							System.out.println(ithUserMovieRating);
							 */
							numSum+=((athUserMovieRating-vaBar)*(ithUserMovieRating-viBar));
							denom1Sum+=Math.pow((athUserMovieRating-vaBar),2);
							denom2Sum+=Math.pow((ithUserMovieRating-viBar),2);
						}
					}
				}
				if(denom1Sum!=0 && denom2Sum!=0)
					wai=numSum/(Math.sqrt(denom1Sum*denom2Sum));
				else
					wai=0;
				if(u2M.get(ithUser.toString().hashCode()).get(moviename)!=null)
				{
					tempSum+=wai*((Double.parseDouble(u2M.get(ithUser.toString().hashCode()).get(moviename)))-viBar);
				}
				waiSum+=wai;
			}
			double k=1/(waiSum);
			double pred=vaBar+((k)*tempSum);
			if(pred==pred){
				meanError+=Math.abs(pred-Double.parseDouble(classVal));
				meanSquareError+=Math.pow((pred-Double.parseDouble(classVal)),2);
				System.out.println("Pred is"+pred+"|"+"Actual is:"+classVal+" |Line value is |"+count+"| MeanError "+(meanError/count)+"| MeanSqError "+(Math.sqrt(meanSquareError/count)));
			}
			else{
				//System.out.println("Nan");
			}
			strnew1=x.readLine();
		}
		meanError=meanError/count;
		meanSquareError=Math.sqrt(meanSquareError/count);
		System.out.println("Final Mean Error is : "+meanError);
		System.out.println("Final RMSE is : "+meanSquareError);
	}

	private static void e() {
		// TODO Auto-generated method stub
		System.exit(1);
	}

	private static void readData() throws Exception {
		//File fileName = new File("C:/Users/darshan/Desktop/MS/Text Books/Machine Learning/Assignment/Assignment 3/netflix/TrainingRatings.txt");
		File fileName = new File(trainingFileName);
		BufferedReader x=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String str=x.readLine();
		String movie="",user="",rating="";
		m2U=new Hashtable<Integer, TreeMap<String,String>>();
		u2M=new Hashtable<Integer, TreeMap<String,String>>();
		sum=new Hashtable<Integer, Double>();
		counter=new Hashtable<Integer, Integer>();
		TreeMap<String, String> s,s1;
		TreeMap<String,String> users;
		Double s2;
		Integer s3;
		while (str!=null) {
			//System.out.println(str);
			String[] strArray=str.split(",");
			movie=strArray[0];
			user=strArray[1];
			rating=strArray[2];
			usersList.add(Integer.parseInt(user));
			/*This is for movie to users mapping*/
			s=m2U.get(movie.hashCode());
			if(s==null){
				users=new TreeMap<String, String>();
				users.put(user,rating);
				m2U.put(movie.hashCode(),users);
			}
			else{
				s.put(user,rating);
			}
			/*This is for users to movie mapping*/
			s1=u2M.get(user.hashCode());
			if(s1==null){
				users=new TreeMap<String, String>();
				users.put(movie,rating);
				u2M.put(user.hashCode(),users);
			}
			else{
				s1.put(movie,rating);
			}
			/*This is for users average*/
			s2=sum.get(user.hashCode());
			if(s2==null)
			{
				sum.put(user.hashCode(),Double.parseDouble(rating));
			}
			else{
				Double temp=s2;
				temp+=Double.parseDouble(rating);
				sum.put(user.hashCode(),temp);
			}

			/*This is for users counter*/
			s3=counter.get(user.hashCode());
			if(s3==null)
			{
				counter.put(user.hashCode(),1);
			}
			else{
				Integer temp=s3;
				temp+=1;
				counter.put(user.hashCode(),temp);
			}
			str=x.readLine();
		}
	}


}
