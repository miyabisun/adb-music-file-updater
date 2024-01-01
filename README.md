# adb-music-file-updater

adbを使ってAndroid内の音楽ファイルを最新の状態に更新します。  
`adb push [PCディレクトリ] [Androidディレクトリ]`やその前段階の確認を便利にやってくれるよというコマンド郡になっています。

## Installation

### adb

使うコマンドは`adb push`と`adb shell`くらいのものなので、  
`apt install android-tools-adb`等のコマンドで導入しても動作するでしょう。

ただ、Android 11 以降はWi-Fi環境越しにadbを使用する`adb pair` -> `adb connect`が使えるようになって便利なので、  
[SDK Platform-Tools リリースノート](https://developer.android.com/studio/releases/platform-tools?hl=ja)からzipファイルをDLして、パスの通ったディレクトリに放り込む導入すると、最新版を扱えます。  
ワイヤレス環境に対応すると幸せになれるかもしれません。

ワイヤレス環境の設定は下記の記事が参考になるでしょう。

- [Android 11で追加されたワイヤレスデバッグが便利だった - Zenn](https://zenn.dev/ik11235/articles/android-wireless-debug)
- [Android 11で追加されたワイヤレスデバッグ adb - Qiita](https://qiita.com/foo4/items/8264b92d5ffa66b4f582)

### babashka

Clojureをローカルで簡易的に動作させるスクリプト言語babashkaを利用しています。  
下記のリンクからbabashkaをインストールしてください。  

- [https://github.com/babashka/babashka?tab=readme-ov-file#quickstart](https://github.com/babashka/babashka?tab=readme-ov-file#quickstart)
- [https://github.com/babashka/babashka/releases](https://github.com/babashka/babashka/releases)

### download

```bash
$ git clone git@github.com:miyabisun/adb-music-file-updater.git

$ cd adb-music-file-updater.git
```


## Usage

### settings.yml の修正

```bash
$ cp settings.sample.yml settings.yml

$ cat settings.yml
dir:
  source: /path/to/music
  dist: /storage/emulated/0/Music
```

- source: PCのディレクトリを指します
  - iTunesのインストール場所を確認してそのPathを格納してください
- dist: Android内のディレクトリを指します
  - 初期値はPixel 7の場合はこのディレクトリに入れてください的な場所です

### 音楽ファイル達のコピー

実行ファイルの `bin/push` を叩くとコピーを実行します。
でもその前に、`bin/check`を実行して、コピーできそうなのか？を確認してみましょう。

```bash
$ bin/check
=== local ===

=> du -hs /path/to/music
39G     /path/to/music

=== android ===

=> df -h /storage/emulated/0/Music
Filesystem      Size Used Avail Use% Mounted on
/dev/fuse       229G  85G  144G  38% /storage/emulated

=> du -hs /storage/emulated/0/Music/aac
0G     /storage/emulated/0/Music
```

今回の例だとAndroid端末のAvail(空き容量)は144GBで、  
ローカルマシンの音楽ディレクトリの必要量は39GBです。  
これによりどうやら全部入れても余裕がありそうだという事がわかります。

それでは、今度こそ `bin/push` コマンドを使いAndroid端末に音楽ファイルを流し込んでいきましょう。

```bash
$ bin/push
/path/to/music/./: 6424 files pushed, 0 skipped. 55.4 MB/s (41166380114 bytes in 709.271s)
finished
```

### その他の便利コマンド

#### has

現在所持しているアルバムの一覧をYAML形式で出力します。  
iTunes は音楽ファイルをディレクトリ区切りで `アーティスト名 / アルバム名 / 楽曲名` の階層構造で管理しています。

```bash
$ bin/has
- {artist: "artist A", album: "album A"}
- {artist: "artist B", album: "album B"}
...

$ bin/has > albums.yml
```

#### now

こちらはAndroid端末内のファイルを確認します。

```bash
$ bin/now
- {artist: "artist A", album: "album A"}
- {artist: "artist B", album: "album B"}
...

$ bin/now > pushed.yml
```

#### clean

:warning: 最悪Android端末の重要ファイルを削除して動作しなくなる危険があります。
十分に設定を確認して実行してください。

`adb shell rm -rf [対象のディレクトリ]/*` コマンドを発行して、
中のファイル全てを掃除します。

### check

`df`や`du`コマンドを利用して、現在の状態を確認します。

```bash
$ bin/check
=== local ===

=> du -hs /path/to/music
39G     /path/to/music

=== android ===

=> df -h /storage/emulated/0/Music
Filesystem      Size Used Avail Use% Mounted on
/dev/fuse       229G  85G  144G  38% /storage/emulated

=> du -hs /storage/emulated/0/Music/aac
0G     /storage/emulated/0/Music
```

