# 前提

クラフト結果を変更する為には

- [CraftItemEvent (bukkit)](https://github.com/sya-ri/spigot-event-list#bukkit-craftitemevent)
- [PrepareItemCraftEvent (bukkit)](https://github.com/sya-ri/spigot-event-list#bukkit-prepareitemcraftevent)

というイベントを使う。

# クラフト結果の簡単な変更

結果表示と結果が同じアイテムであれば、`PrepareItemCraftEvent` のみを使うだけで変更することができる。

`PrepareItemCraftEvent` が呼び出されるのは、材料を変更し結果を更新する時になる。
このイベントが呼び出された時に、結果スロットのアイテムを変更すればクラフト結果を変更することができる。

`PrepareItemCraftEvent::getRecipe` の戻り値は `Recipe?` である。
このイベントは、結果を更新する時に呼び出される為、存在しないレシピでもイベントが呼び出されてしまう。

```kotlin
event<PrepareItemCraftEvent> {
    if (it.recipe != null) { // レシピとして成立している時のみ
        it.inventory.result = ItemStack(/* ... */)
    }
}
```

戻り値が `null` の時に上書きしなければ、結果が存在する場合だけ上書きすることができる。

# 結果表示を別のアイテムにして変更する

結果表示と結果を別のアイテムにする場合、`PrepareItemCraftEvent` で結果表示を行い、`CraftItemEvent` で結果を変更する。

[クラフト結果の簡単な変更](#クラフト結果の簡単な変更) の方法で結果表示を変更する。

結果を変更するのは `CraftItemEvent` になる。このイベントは、結果スロットをクリックした時にアイテムが存在すれば呼び出される。
インベントリが空いていない状態でクリックしても呼び出されるので、クラフトできない場合も呼び出されてしまう。以下の場合はクラフトできない状態と考えられる。

- シフトでクラフトを行い、インベントリが空いていなかった時
- ナンバーキーでクラフトを行い、押したキーに対応したホットバーが空いていなかった時
- アイテムを手に持った状態で結果スロットをクリックした時

それをコードに書き起こすと以下の様になる。

```kotlin
event<CraftItemEvent> {
    val result = ItemStack(/* ... */)
    if (it.isShiftClick) {
        if (it.whoClicked.inventory.firstEmpty() != -1) {
            it.currentItem = result
        }
    } else {
        val item = if (it.click == ClickType.NUMBER_KEY) {
            it.whoClicked.inventory.getItem(it.hotbarButton)
        } else {
            it.cursor
        }
        if (item == null || item.type == Material.AIR) {
            it.inventory.result = result
        }
    }
}
```

- インベントリに空きがない場合、`Inventory::firstEmpty` は `-1` を返す。
- `InventoryClickEvent::getClick` が `ClickType.NUMBER_KEY` の時、`InventoryClickEvent::getHotbarButton` は `0~8` を返す。
- 取得したアイテムが `null` だった時、または `ItemStack::getType` が `Material.AIR` だった時はインベントリスロットは空であると言える。
