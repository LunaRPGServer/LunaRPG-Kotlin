package me.syari.menu.party

import me.syari.menu.MainMenu
import me.syari.menu.Menu
import me.syari.server.Board
import me.syari.server.Get
import me.syari.server.Send
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object PartyJoin {
    private val menu = Menu.Inv.PartyJoin
    private val DisplayPlayerNumber = (menu.line - 1) * 9
    private enum class Item(val slot: Int, val display: String){
        Prev(menu.line * 9 - 9, "&f&l<<"),
        Create(menu.line * 9 - 7, "&6パーティー作成"),
        ShowPublic(menu.line * 9 - 6, "&6公開パーティー表示"),
        Back(menu.line * 9 - 4, "&f&l戻る"),
        Next(menu.line * 9 - 1, "&f&l>>")
    }

    private fun Inventory.set(item: Item, material: Material, vararg lore: String){
        setItem(item.slot, Get.Item.item(item.display, material, *lore))
    }

    fun open(p: Player, page: Int = 1, showPublic: Boolean = true){
        val u = Users.getUser(p)
        if(menu.onlyAdmin && ! u.isAdmin()) return
        val inv = Menu.getMenu(menu)
        val dl = Users.getParties().filter { pd -> (pd.hasInvite(p) || (showPublic && pd.isPublic())) && pd.getMember().size < PartyList.maxMember && pd.getInvite().size < PartyList.maxInvite }
        var c = 0
        for(o in dl.withIndex()){
            if(c < DisplayPlayerNumber && ((page - 1) * DisplayPlayerNumber) <= o.index){
                val d = dl.getOrNull(c) ?: continue
                inv.setItem(c, Get.Item.item("&6${d.getLeader().name}   &e${d.getMember().size} &7/ &e${PartyList.maxMember}", d.getIcon(), "&aパーティーに入る"))
                c ++
            }
        }
        if(page > 1){
            inv.set(Item.Prev, Material.WHITE_STAINED_GLASS_PANE, "&a${page - 1} &7ページ")
        }
        if((c + 1) == DisplayPlayerNumber){
            inv.set(Item.Next, Material.WHITE_STAINED_GLASS_PANE, "&a${page + 1} &7ページ")
        }
        inv.set(Item.Create, Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                "&f >> &aパーティーを作成します ", "&b 左クリック &7: &b公開パーティー ",
                "&b 右クリック &7: &b非公開パーティー "
        )
        inv.set(Item.ShowPublic, getShowPublicType(showPublic),
                "&7現在の設定 : ${if(showPublic) "&a表示" else "&c非表示"}")
        inv.set(Item.Back, Material.RED_STAINED_GLASS_PANE)
        p.openInventory(inv)
    }

    fun click(e: InventoryClickEvent){
        e.isCancelled = true
        if (Menu.isSame(e)) {
            val p = e.whoClicked as Player
            val u = Users.getUser(p)
            val slot = Get.Inv.targetSlot(e)
            val inv = e.inventory ?: return
            val item = getClick(e)
            when(item) {
                Item.Create -> {
                    if(u.isPartyBelong()) {
                        PartyList.open(p)
                    } else {
                        val pub = when {
                            e.isLeftClick -> true
                            e.isRightClick -> false
                            else -> return
                        }
                        u.createParty(pub)
                        PartyList.open(p)
                        if(pub){
                            reopen()
                        }
                        Send.msg(p, "パーティーを作成しました")
                        Board.Score.set(p)
                    }
                }
                Item.ShowPublic -> {
                    val now = Get.Inv.slotType(inv, slot) == Material.BLUE_STAINED_GLASS_PANE
                    open(p, 1, !now)
                }
                Item.Prev -> {
                    val txt = Get.Inv.slotLore(inv, slot).getOrNull(0) ?: return
                    val page = Get.Number.int(txt)
                    open(p, page)
                }
                Item.Next -> {
                    val txt = Get.Inv.slotLore(inv, slot).getOrNull(0) ?: return
                    val page = Get.Number.int(txt)
                    open(p, page)
                }
                Item.Back -> {
                    MainMenu.open(p)
                }
            }
        }
    }

    fun reopen(p: Player){
        if(Menu.equalOpenInventoryTitleAndMenu(p, Menu.Inv.PartyJoin)){
            val inv = p.openInventory.topInventory
            val txt = Get.Inv.slotLore(inv, Item.Next.slot).getOrNull(0) ?: return
            val nextPage = Get.Number.int(txt)
            val showPublic = Get.Inv.slotType(inv, Item.ShowPublic.slot) == getShowPublicType(true)
            open(p, nextPage - 1, showPublic)
        }
    }

    fun reopen(l: List<Player> = Get.onlinePlayer()){
        for (p in l) {
            reopen(p)
        }
    }

    private fun getClick(e: InventoryClickEvent) : Item? {
        val slot = Get.Inv.targetSlot(e)
        return Item.values().firstOrNull { v -> v.slot == slot }
    }

    private fun getShowPublicType(showPublic: Boolean): Material{
        return if(showPublic) Material.BLUE_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE
    }
}