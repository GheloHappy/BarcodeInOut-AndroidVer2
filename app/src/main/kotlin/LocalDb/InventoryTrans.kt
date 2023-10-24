package LocalDb

import net.sourceforge.jtds.jdbc.DateTime
import java.util.Date

data class InventoryTrans(val barcode: String, val solomonID: String, val uom: String, val qty: Int, val date: Date, val date_entry: DateTime, val username: String,
                                val description: String, val refNbr: String, val remarks: String, val warehouse: String)
