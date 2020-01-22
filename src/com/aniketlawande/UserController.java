package com.aniketlawande;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aniketlawande.dao.UserDao;
import com.aniketlawande.model.OperationResponse;
import com.aniketlawande.model.UserProfile;
import com.aniketlawande.model.UserRequest;

/**
 * User controller methods
 * @author Aniket
 *
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
	
	/**
	 * Gets the current user from the session
	 * @param request
	 * @return
	 */
	@RequestMapping("/current")
	public UserProfile getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("user") == null)
			return new UserProfile();
		UserProfile user = new UserProfile();
		user.setUserId(session.getAttribute("user").toString());
		return user;
	}
	
	/**
	 * Sign in the user using the provided firstname and lastname
	 * @param request
	 * @param userreq
	 * @return
	 */
	@RequestMapping(value="/signIn", method=RequestMethod.POST)
	public UserProfile signInUser(HttpServletRequest request, @RequestBody UserRequest userreq) {
		HttpSession session = request.getSession();
		String firstname = userreq.getFirstname();
		String lastname = userreq.getLastname();
		if (firstname == null || lastname == null)
			return new UserProfile();
		
		String userId = firstname + "." + lastname;
		session.setAttribute("user", userId);
		UserProfile user = new UserProfile();
		user.setUserId(userId);
		user.setFirstname(firstname);
		user.setLastname(lastname);
		
		UserDao.setUserProfile(user);
		
		return user;
	}
	
	/**
	 * Gets the user profile for the userId specfied
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public UserProfile getProfileUser(HttpServletRequest req) {
		String userId = req.getParameter("userId");
		UserProfile user = UserDao.getUserProfile(userId);
		if (user == null) {
			// Good place to throw a 404
			user = new UserProfile();
			return user;
		}
		return user;
	}
	
	/**
	 * Sets the user profile for the userId specified
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public OperationResponse setProfileUser(@RequestBody UserProfile req) {
		//Need to do some basic authorization
		return UserDao.setUserProfile(req);
	}
	
	/**
	 * Logs out the current user
	 * @param request
	 * @return
	 */
	@RequestMapping("/logout")
	public OperationResponse logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		
		OperationResponse op = new OperationResponse();
		op.setOpCode("SUCCESS");
		
		return op;
	}
}
