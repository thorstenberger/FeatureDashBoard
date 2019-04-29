import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimplTest {
	public static void main(String[] args) {
		nullStreamCheck();
	}
	
	public static void nullStreamCheck() {
		List<String> names = new ArrayList<String>();
		names.add("sina");
		names.add("ali");
		
		List<String> ans = names.stream().filter(name->(name.equals("barabar"))).collect(Collectors.toList());
		if(ans ==null)
			System.out.println("null");
		else
			System.out.println("size:"+ans.size());
	}

}
