/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelmaster.gallery;

import hotelmaster.Photo;
import java.util.List;

/**
 *
 * @author Trevor
 */
public interface PhotoDAOInterface {
    List<Photo> list();
}