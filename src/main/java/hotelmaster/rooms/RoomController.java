package hotelmaster.rooms;

import hotelmaster.Room;
import hotelmaster.account.AccountLevel;
import hotelmaster.account.AccountSession;
import hotelmaster.gallery.PhotoDAO;
import hotelmaster.notification.NotificationService;
import hotelmaster.notification.NotificationType;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Danny
 */

@Controller
public class RoomController {

    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private PhotoDAO photoDAO;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AccountSession accountSession;
    
    
    @RequestMapping(value = "/admin/rooms")
    public ModelAndView listRoom(ModelAndView model) throws IOException {
        
        if (accountSession.getAccount() == null || accountSession.getAccount().getAccountLevel() == AccountLevel.USER) {
            notificationService.add("Error!", "You do not have the required permissions to access the page", NotificationType.ERROR);
            return new ModelAndView("redirect:/home");
        }
        
        //add notification handling to this page
        model.addObject("notificationService", notificationService);
        
        List<Room> roomList = roomDAO.list();
        RoomForm rf = new RoomForm();
        List<String> features = roomDAO.listFeatures();
        roomDAO.setRoomFeatures(roomList);
        
        model.addObject("roomList", roomList);
        model.addObject("roomForm", new RoomForm());
        model.addObject("features", features);
        model.setViewName("admin/rooms");
               
        return model;
    }
    
    @RequestMapping(value = "/admin/rooms", method = RequestMethod.POST)
    public ModelAndView addRoom(@ModelAttribute("roomForm") Room room, ModelAndView model, @RequestParam String action, BindingResult br){
        Room r = new Room();
        
        r.setRoomName(room.getRoomName());
        r.setFloor(room.getFloor());
        r.setPricePerNight(room.getPricePerNight());
        r.setMaxGuests(room.getMaxGuests());
        r.setFeaturesTest(room.getFeaturesTest());
        
        Set<String> selectedFeatures = room.getFeaturesTest();
        
        if (selectedFeatures != null){
            Iterator it = selectedFeatures.iterator();
            while (it.hasNext()){
                r.getFeatures().put((String) it.next(), Boolean.TRUE);
            }
        
            r.setFeatures(r.getFeatures());
        }                
        
        if (action.equalsIgnoreCase("add")){
            roomDAO.insertRoom(r);
            r.setRoomID(roomDAO.getMaxRoomID());   
            roomDAO.addRoomFeatures(r);
            notificationService.add("Great!", "Successfully added room!", NotificationType.SUCCESS);
        }
        else if (action.equalsIgnoreCase("delete")){
            r.setRoomID(room.getRoomID());
            roomDAO.deleteRoom(room);   
            notificationService.add("Great!", "Successfully deleted room!", NotificationType.SUCCESS);
        }
        else if (action.equalsIgnoreCase("edit")){
            r.setRoomID(room.getRoomID());
            roomDAO.updateRoom(room);
            
            r.setFeaturesTest(room.getFeaturesTest());

            Iterator it = selectedFeatures.iterator();
            while (it.hasNext()){
                r.getFeatures().put((String) it.next(), Boolean.TRUE);
            }

            r.setFeatures(r.getFeatures());
            
            roomDAO.addRoomFeatures(r);
            notificationService.add("Great!", "Successfully edited room!", NotificationType.SUCCESS);
        }

        //add notification handling to this page
        model.addObject("notificationService", notificationService);
            
        List<Room> roomList = roomDAO.list();
        RoomForm rf = new RoomForm();
        List<String> features = roomDAO.listFeatures();
        roomDAO.setRoomFeatures(roomList);
        
        model.addObject("roomList", roomList);
        model.addObject("roomForm", new RoomForm());
        model.addObject("features", features);
        model.setViewName("admin/rooms");
        
        return model;
    }
}
