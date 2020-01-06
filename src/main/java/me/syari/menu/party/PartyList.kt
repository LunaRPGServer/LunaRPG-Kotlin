package me.syari.menu.party

import me.syari.menu.MainMenu
import me.syari.menu.Menu
import me.syari.server.Get
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object PartyList {
    const val maxMember = 5
    const val maxInvite = 7
    private const val beginMemberSlot = 2
    private const val endMemberSlot = beginMemberSlot + maxMember - 1
    private const val beginInviteSlot = 19
    private const val endInviteSlot = beginInviteSlot + maxInvite - 1

    private val menu = Menu.Inv.PartyList
    private enum class Item(val slot: Int, val display: String){
        Info(0, "&6パーティー情報"),
        Public(menu.line * 9 - 9, "&6公開設定"),
        Edit(menu.line * 9 - 8, "&6メンバー編集"),
        Invite(menu.line * 9 - 7, "&6プレイヤー招待"),
        Icon(menu.line * 9 - 6, "&6パーティーアイコン変更"),
        Back(menu.line * 9 - 4, "&f&l戻る"),
        Leave(menu.line * 9 - 1, "&6パーティー脱退")
    }

    enum class EditMode(val type: Material, val desc: String) {
        None(Material.WHITE_STAINED_GLASS_PANE, "&f通常モード"),
        Remove(Material.RED_STAINED_GLASS_PANE, "&c招待取消・除名モード"),
        Transfer(Material.BLUE_STAINED_GLASS_PANE, "&9リーダー譲渡モード")
    }

    private fun Inventory.set(item: Item, material: Material, vararg lore: String){
        setItem(item.slot, Get.Item.item(item.display, material, *lore))
    }

    fun open(p: Player, mode: EditMode = EditMode.None){
        val u = Users.getUser(p)
        if(menu.onlyAdmin && ! u.isAdmin()) return
        val d = u.getParty() ?: return
        val inv = Menu.getMenu(menu)
        if(d.isLeader(p)){
            inv.set(Item.Public, Material.WHITE_STAINED_GLASS_PANE, "&7現在の設定 : ${if (d.isPublic()) "&a公開" else "&c非公開"}パーティー")
            inv.set(Item.Edit, mode.type, "&7現在の設定 : ${mode.desc}")
            inv.set(Item.Invite, Material.WHITE_STAINED_GLASS_PANE, "&a招待するプレイヤーを選択します")
            inv.set(Item.Icon, Material.WHITE_STAINED_GLASS_PANE, "&aパーティーアイコンを選択します")
        }
        inv.set(Item.Info, d.getIcon(),
                "&7リーダー : &a${d.getLeader().name}",
                "&aパーティー : ${if (d.isPublic()) "&a公開" else "&c非公開"}",
                "&7所属 : &a${d.getMember().size} &7/ &a$maxMember",
                "&7招待 : &a${d.getInvite().size} &7/ &a$maxInvite"
        )
        inv.set(Item.Leave, Material.BLUE_STAINED_GLASS_PANE, "&a所属しているパーティーから抜けます")
        inv.set(Item.Back, Material.RED_STAINED_GLASS_PANE)
        for(m in d.getMember().withIndex()){
            inv.setItem(m.index + beginMemberSlot, Get.Item.head("&6${m.value.name}", m.value, "&a所属メンバー"))
        }
        for(m in d.getInvite().withIndex()){
            inv.setItem(m.index + beginInviteSlot, Get.Item.head("&6${m.value.name}", m.value, "&a招待プレイヤー"))
        }
        p.openInventory(inv)
    }

    fun click(e: InventoryClickEvent){
        e.isCancelled = true
        if (Menu.isSame(e)) {
            val p = e.whoClicked as Player
            val u = Users.getUser(p)
            val d = u.getParty() ?: return
            val slot = Get.Inv.targetSlot(e)
            val inv = e.inventory
            when(slot) {
                in beginMemberSlot .. endMemberSlot -> {
                    val s = Get.Inv.clickSlotDisplayName(e)
                    val t = Get.player(Get.Color.uncolor(s)) ?: return
                    val mode = getMode(inv)
                    when(mode){
                        EditMode.None -> return
                        EditMode.Remove -> {
                            d.removeMember(t)
                            // TODO msg
                        }
                        EditMode.Transfer -> {
                            d.setLeader(t)
                            // TODO msg
                        }
                    }
                    PartyList.reopen(d.getMember())
                    if(d.isPublic()){
                        PartyJoin.reopen()
                    } else {
                        PartyJoin.reopen(d.getInvite())
                    }
                }
                in beginInviteSlot .. endInviteSlot -> {
                    val mode = getMode(inv)
                    if(mode == EditMode.Remove) {
                        val s = Get.Inv.clickSlotDisplayName(e)
                        val t = Get.player(Get.Color.uncolor(s)) ?: return
                        d.removeInvite(t)
                        // TODO msg
                    }
                }
                else -> {
                    val item = getClick(e)
                    when(item) {
                        Item.Info -> {
                            return
                        }
                        Item.Public -> {
                            d.togglePublic()
                            d.clearInvite()
                            PartyJoin.reopen()
                            PartyList.reopen(p)
                        }
                        Item.Edit -> {
                            // TODO
                        }
                        Item.Invite -> {
                            PartyInvite.open(p)
                        }
                        Item.Icon -> {
                            PartyIcon.open(p)
                        }
                        Item.Leave -> {
                            u.leaveParty()
                            MainMenu.open(p)
                        }
                        Item.Back -> {
                            MainMenu.open(p)
                        }
                    }
                }
            }
        }
    }

    fun reopen(p: Player){
        if(Menu.equalOpenInventoryTitleAndMenu(p, Menu.Inv.PartyList)){
            val inv = p.openInventory.topInventory
            val mode = getMode(inv)
            open(p, mode)
        }
    }

    fun reopen(l: List<Player> = Get.onlinePlayer()){
        for(p in l) {
            reopen(p)
        }
    }

    private fun getClick(e: InventoryClickEvent) : Item? {
        val slot = Get.Inv.targetSlot(e)
        return Item.values().firstOrNull { v -> v.slot == slot }
    }

    private fun getMode(inv: Inventory) : EditMode {
        val editSlot = Get.Inv.slotType(inv, Item.Edit.slot)
        return EditMode.values().firstOrNull { v -> v.type == editSlot } ?: EditMode.None
    }
}