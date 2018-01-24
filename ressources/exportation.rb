#------------------------------------------------------------------
# Dans un premier temps on cree une methode string? 
# qui permet de savoir si un objet est une chaine ou pas
#------------------------------------------------------------------
# elle vaudra faux pour tous les objets banals
class Object
  def string?
    return false
  end
end
# mais elle vaudra vrai pour les chaines
class String
  def string?
    true
  end
end

#--------------------------------------------------------------------------

class Exportation
  
  MapsFile = "./Data/MapInfos.rxdata"
  TilesetsFile = "./Data/Tilesets.rxdata"
  ItemsFile = "./Data/Items.rxdata"
  QuestsFile = "./Data/Skills.rxdata"
  EXPORT_FOLDER = "./Exportation/Maps"
  EXPORT_TILESET_FOLDER = "./Exportation/Tilesets"
  EXPORT_MAIN_FOLDER = "./Exportation"
  
  #------------------------------------------------------------------
  # Ecrit un retour a la ligne
  #------------------------------------------------------------------
  def write_linebreak(file)
    file.write("\r\n")
  end
  
  #----------------------------------------------------
  # Fonction principale, elle exporte les maps en json
  #----------------------------------------------------
  def exporter

    #----------------------
    # Exportation des maps
    #----------------------
    maps = load_data(MapsFile)
    tilesets = load_data(TilesetsFile)

    # Si le dossier d'exportation n'existe pas, on le cree
    if !File.directory?(EXPORT_FOLDER)
      Dir.mkdir(EXPORT_FOLDER)
    end
    
    # On parcourt toutes les maps
    for map_id in 1...maps.size+1
      mapFile = sprintf("Data/Map%03d.rxdata", map_id)
      map = load_data(mapFile) rescue next
      
      mapInfo = maps[map_id]
      
      
      # On cree un fichier par map
      filename = sprintf("%s/%03d_%s.json", EXPORT_FOLDER, map_id, mapInfo.name)
      #filename = sprintf("%s/%d.json", EXPORT_FOLDER, map_id)
      # On supprime le fichier s'il existe deja
      if File.exist?(filename)
        File.delete(filename)
      end
      # Ouvre le fichier et rajoute du texte a la fin
      file = File.new(filename, 'a')
      
      # Debut de l'ecriture de la map
      file.write("{")
      write_linebreak(file)
      
      # Nom de la map
      file.write(sprintf("\t\"nom\": \"%s\",", mapInfo.name))
      write_linebreak(file)
      
      # Tileset de la map
      file.write(sprintf("\t\"tileset\": \"%s\",", tilesets[map.tileset_id].name))
      write_linebreak(file)
      
      # Largeur de la map
      file.write(sprintf("\t\"largeur\": %s,", map.width))
      write_linebreak(file)
      # Hauteur de la map
      file.write(sprintf("\t\"hauteur\": %s,", map.height))
      write_linebreak(file)
      
      # Musique de la map
      if map.bgm != nil
        nomFichier = map.bgm.name
        extension = File.extname(Dir.entries("./Audio/BGM").select{|s| s.index(nomFichier+'.') == 0}.first)
        file.write("\t\"musique\": {")
        write_linebreak(file)
        file.write(sprintf("\t\t\"nomFichierSonore\": \"%s\",", nomFichier+extension))
        write_linebreak(file)
        file.write(sprintf("\t\t\"volume\": %.2f,", map.bgm.volume.to_f/100))
        write_linebreak(file)
        file.write(sprintf("\t\t\"tempo\": %.2f", map.bgm.pitch.to_f/100))
        write_linebreak(file)
        file.write("\t},")
        write_linebreak(file)
      end
      if map.bgs != nil
        nomFichier = map.bgs.name
        extension = File.extname(Dir.entries("./Audio/BGS").select{|s| s.index(nomFichier+'.') == 0}.first)
        file.write("\t\"fondSonore\": {")
        write_linebreak(file)
        file.write(sprintf("\t\t\"nomFichierSonore\": \"%s\",", nomFichier+extension))
        write_linebreak(file)
        file.write(sprintf("\t\t\"volume\": %.2f,", map.bgs.volume.to_f/100))
        write_linebreak(file)
        file.write(sprintf("\t\t\"tempo\": %.2f", map.bgs.pitch.to_f/100))
        write_linebreak(file)
        file.write("\t},")
        write_linebreak(file)
      end
      
      # Couches de decor
      for couche in [0, 1, 2]
        file.write(sprintf("\t\"couche%d\": [", couche))
        write_linebreak(file)
        for y in 0...map.height
          ligne = "\t["
          for x in 0...map.width
            # On recupere la case
            tile_id = normaliser_tile_id( map.data[x, y, couche] )
            ligne += sprintf("%04d", tile_id)
            # La derniere colonne n'a pas de virgule
            if x < map.width - 1
              ligne += ","
            end
          end
          ligne += "]"
          # La derniere ligne n'a pas de virgule
          if y < map.height - 1
            ligne += ","
          end
          file.write(ligne)
          write_linebreak(file)
        end
        file.write("\t],")
        write_linebreak(file)
      end
      # Fin des couches de decor
      
      file.write("\t\"events\": [")
      write_linebreak(file)
      # On parcourt tous les events
      for event_id in map.events.keys
        event = map.events[event_id]
        
        # Debut de l'ecriture de l'event
        file.write("\t{")
        write_linebreak(file)
        
        # Id de l'event
        file.write(sprintf("\t\t\"id\": %d,", event_id))
        write_linebreak(file)
        
        # Nom de l'event
        file.write(sprintf("\t\t\"nom\": \"%s\",", event.name))
        write_linebreak(file)
        
        # Position initiale de l'event
        file.write(sprintf("\t\t\"x\": %d,", event.x))
        write_linebreak(file)
        file.write(sprintf("\t\t\"y\": %d", event.y))
        
        
        # Un event generique n'a pas de pages
        if !event.name.include? "Clone"
          file.write(",")
          write_linebreak(file)
          
          file.write("\t\t\"pages\": [")
          write_linebreak(file)
          # On parcourt toutes les pages
          for i in 0...event.pages.size

            # Debut de l'ecriture de la page
            file.write("\t\t{")
            write_linebreak(file)          
            
            page = event.pages[i]
            
            
            # Ecriture des conditions de la page
            file.write("\t\t\t\"conditions\": [")
            write_linebreak(file)
            
            c = page.condition
            
            # Condition de declenchement
            case page.trigger
              when 0
                # Parler
                file.write("\t\t\t{")
                write_linebreak(file)
                file.write("\t\t\t\t\"nom\": \"Parler\"")
                write_linebreak(file)
                file.write("\t\t\t}")
                if c.switch1_valid || c.switch2_valid || c.variable_valid || c.self_switch_valid
                  file.write(",")
                end
                write_linebreak(file)
              when 1
                # Arrivee au contact
                file.write("\t\t\t{")
                write_linebreak(file)
                file.write("\t\t\t\t\"nom\": \"ArriveeAuContact\"")
                write_linebreak(file)
                file.write("\t\t\t}")
                if c.switch1_valid || c.switch2_valid || c.variable_valid || c.self_switch_valid
                  file.write(",")
                end
                write_linebreak(file)
              when 2
                # Contact
                file.write("\t\t\t{")
                write_linebreak(file)
                file.write("\t\t\t\t\"nom\": \"Contact\"")
                write_linebreak(file)
                file.write("\t\t\t}")
                write_linebreak(file)
              when 3
                # Automatique
                # rien, mais fige les autres events
              when 4
                # Processu parallele
                # rien
            end
              
            # Condition 1 (interrupteur)
            if c.switch1_valid
              
              file.write("\t\t\t{")
              write_linebreak(file)
              file.write("\t\t\t\t\"nom\": \"Interrupteur\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numeroInterrupteur\": %d,", c.switch1_id))
              write_linebreak(file)
              file.write("\t\t\t\t\"valeurQuIlEstCenseAvoir\": true")
              write_linebreak(file)
              file.write("\t\t\t}")
              
              if c.switch2_valid || c.variable_valid || c.self_switch_valid
                file.write(",")
              end
              write_linebreak(file)
            end

            # Condition 2 (interrupteur)
            if c.switch2_valid
              file.write("\t\t\t{")
              write_linebreak(file)
              file.write("\t\t\t\t\"nom\": \"Interrupteur\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numeroInterrupteur\": %d,", c.switch2_id))
              write_linebreak(file)
              file.write("\t\t\t\t\"valeurQuIlEstCenseAvoir\": true")
              write_linebreak(file)
              file.write("\t\t\t}")
              
              if c.variable_valid || c.self_switch_valid
                file.write(",")
              end
              write_linebreak(file)
            end
            
            # Condition 3 (variable)
            if c.variable_valid
              file.write("\t\t\t{")
              write_linebreak(file)
              file.write("\t\t\t\t\"nom\": \"Variable\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numeroVariable\": %d,", c.variable_id))
              write_linebreak(file)
              file.write("\t\t\t\t\"inegalite\": \">=\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"valeurQuIlEstCenseAvoir\": %d", c.variable_value))
              write_linebreak(file)
              file.write("\t\t\t}")
              
              if c.self_switch_valid
                file.write(",")
              end
              write_linebreak(file)
            end
            
            # Condition 4 (interrupteur local)
            if c.self_switch_valid
              file.write("\t\t\t{")
              write_linebreak(file)
              file.write("\t\t\t\t\"nom\": \"InterrupteurLocal\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numeroMap\": %d,", map_id))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numeroEvent\": %d,", event_id))
              write_linebreak(file)
              numero_interrupteur_local = case c.self_switch_ch
                 when "A" then 0
                 when "B" then 1
                 when "C" then 2
                 when "D" then 3
                 else -1
              end
              file.write(sprintf("\t\t\t\t\"numeroInterrupteurLocal\": %s,", numero_interrupteur_local))
              write_linebreak(file)
              file.write("\t\t\t\t\"valeurQuIlEstCenseAvoir\": true")
              write_linebreak(file)
              file.write("\t\t\t}")
              write_linebreak(file)
            end

            file.write("\t\t\t],")
            write_linebreak(file)        
            # Fin des conditions de la page
            
            
            # Ecriture des commandes de la page
            write_commands(file, page)
            # Fin des commandes de la page
            
            
            # Ecriture des mouvements de la page
            if page.move_type != 0
              file.write("\t\t\t\"deplacement\": {")
              write_linebreak(file)
              file.write("\t\t\t\t\"nom\": \"Deplacement\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"ignorerLesMouvementsImpossibles\": %s,", page.move_route.skippable))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"repeterLeDeplacement\": %s,", page.move_route.repeat))
              write_linebreak(file)
              file.write("\t\t\t\t\"attendreLaFinDuDeplacement\": false,")
              write_linebreak(file)
              file.write("\t\t\t\t\"mouvements\": [")
              write_linebreak(file)
              case page.move_type
              when 1
                # Aleatoire
                file.write("\t\t\t\t\t{")
                write_linebreak(file)
                file.write("\t\t\t\t\t\t\"nom\": \"AvancerAleatoirement\"")
                file.write("\t\t\t}")
              when 2
                # Suit le heros
                file.write("\t\t\t\t\t{")
                write_linebreak(file)
                file.write("\t\t\t\t\t\t\"nom\": \"AvancerEnFonctionDUnEvent\",")
                file.write("\t\t\t\t\t\t\"idEventObserve\": 0")
                file.write("\t\t\t\t\t}")
              when 3
                # Parcours predefini
                i_mouvement = 0
                mouvement = page.move_route.list[i_mouvement]
                while mouvement != nil
                  ignorer_ce_mouvement = (mouvement.code == 0)
                  if !ignorer_ce_mouvement
                    ecrire_mouvement(mouvement, file)
                  end
                  i_mouvement += 1
                  mouvement = page.move_route.list[i_mouvement]
                  if mouvement != nil && !ignorer_ce_mouvement
                    file.write(",")
                    write_linebreak(file)
                  end
                end
              end
              write_linebreak(file)
              file.write("\t\t\t\t],")
              write_linebreak(file)
              file.write("\t\t\t},")
              write_linebreak(file)
            end
            # Fin des mouvements de la page
            
            
            # Ecriture des proprietes de la page
            
            # Apparence
            apparence_est_un_tile = false
            if page.graphic.character_name.to_s.empty?
              # L'apparence est un tile
              apparence_est_un_tile = true
              tile_id = normaliser_tile_id(page.graphic.tile_id)
              if tile_id != -1
                file.write(sprintf("\t\t\t\"image\": %d,", tile_id))
                write_linebreak(file)
              end
            else
              # L'apparence est une image
              file.write(sprintf("\t\t\t\"image\": \"%s\",", page.graphic.character_name))
              write_linebreak(file)
            end
            
            # Teinte
            if page.graphic.character_hue != 0
              file.write(sprintf("\t\t\t\"teinte\": \"%s\",", page.graphic.character_hue))
              write_linebreak(file)
            end
            
            # Opacite
            if page.graphic.opacity != 255
              file.write(sprintf("\t\t\t\"opacite\": %d,", page.graphic.opacity))
              write_linebreak(file)
            end
            
            # Mode de fusion
            if page.graphic.blend_type != 0
              file.write(sprintf("\t\t\t\"modeDeFusion\": %d,", page.graphic.blend_type))
              write_linebreak(file)
            end
            
            # Direction initiale
            if page.graphic.direction != 2 # bas est la direction initiale par defaut
              directionInitiale = case page.graphic.direction
                  when 2 then 0
                  when 4 then 1
                  when 6 then 2
                  when 8 then 3
                end
              file.write(sprintf("\t\t\t\"directionInitiale\": %d,", directionInitiale))
              write_linebreak(file)
            end
            
            # Animation initiale
            if page.graphic.pattern != 0
              file.write(sprintf("\t\t\t\"animationInitiale\": %d,", page.graphic.pattern))
              write_linebreak(file)
            end
            
            # Vitesse
            vitesse = case page.move_speed
              when 0 then 1
              when 1 then 2
              when 2 then 4
              when 3 then 8
              when 4 then 12
              when 5 then 16
              else        24
            end
            file.write(sprintf("\t\t\t\"vitesse\": %d,", vitesse))
            write_linebreak(file)
            
            # Frequence
            frequence = case page.move_frequency
              when 0 then 32
              when 1 then 24
              when 2 then 16
              when 3 then 8
              when 4 then 4
              when 5 then 2
              else        1
            end
            file.write(sprintf("\t\t\t\"frequence\": %d,", frequence))
            write_linebreak(file)
            
            # Fige les autres events
            if page.trigger == 3 # Automatique
              file.write("\t\t\t\"figerLesAutresEvents\": true,")
              write_linebreak(file)
            end
            
            # Anime en mouvement
            file.write(sprintf("\t\t\t\"animeEnMouvement\": %s,", page.walk_anime))
            write_linebreak(file)
            
            # Anime a l'arret
            file.write(sprintf("\t\t\t\"animeALArret\": %s,", page.step_anime))
            write_linebreak(file)
            
            # Direction fixe
            file.write(sprintf("\t\t\t\"directionFixe\": %s,", page.direction_fix))
            write_linebreak(file)
            
            # Traversable
            file.write(sprintf("\t\t\t\"traversable\": %s,", page.through))
            write_linebreak(file)
            
            # Au dessus de tout
            file.write(sprintf("\t\t\t\"auDessusDeTout\": %s", page.always_on_top))
            write_linebreak(file)
            # Fin des proprietes de la page
          
            
            # Fin de l'ecriture de la page
            file.write("\t\t}")
            if i < event.pages.size - 1
                file.write(",")
            end
            write_linebreak(file)
              
            # On rajoute ca pour ne pas que le programme plante lol
            Graphics.update
          
          end
          # Fin du parcours des pages
          file.write("\t\t]")
        end
        write_linebreak(file)
        
        
        # Fin de l'ecriture de l'event
        file.write("\t}")
        if event_id != map.events.keys.last
          file.write(",")
        end
        write_linebreak(file)
        
      end
      # Fin du parcours de events
      file.write("\t]")
      write_linebreak(file)
    
    # Fin de l'ecriture de la map
    file.write("}")
    write_linebreak(file)
    
    # On referme le fichier de la map
    file.close
    
    end
    # Fin du parcours des maps
    
    # Fin de l'exportation des maps

    
    #--------------------------
    # Exportation des Tilesets
    #--------------------------
    
    # Si le dossier d'exportation n'existe pas, on le cree
    if !File.directory?(EXPORT_TILESET_FOLDER)
      Dir.mkdir(EXPORT_TILESET_FOLDER)
    end
    
    # On parcourt tous les tilesets
    for tileset_id in 1...tilesets.size
      tilesetInfo = tilesets[tileset_id]

      if tilesetInfo.name != ""
        # On cree un fichier par tileset
        filename = sprintf("%s/%s.json", EXPORT_TILESET_FOLDER, tilesetInfo.name)
        # On supprime le fichier s'il existe deja
        if File.exist?(filename)
          File.delete(filename)
        end
        # Ouvre le fichier et rajoute du texte a la fin
        file = File.new(filename, 'a')
        
        # Debut de l'ecriture du tileset
        file.write("{")
        write_linebreak(file)
        
        # Nom de l'image
        file.write(sprintf("\t\"nomImage\": \"%s\",", tilesetInfo.tileset_name))
        write_linebreak(file)
        
        # Passabilites
        file.write("\t\"passabilite\": [")
        tile_id = 384
        while tilesetInfo.passages[tile_id] != nil
          # Virgule entre les valeurs
          if tile_id != 384
            file.write(", ")
          end
          # Retour a la ligne
          if tile_id % 8 == 0
            write_linebreak(file)
            file.write("\t\t")
          end
          # Blocages individuels : 8 haut + 4 droite + 2 gauche + 1 bas
          passage = tilesetInfo.passages[tile_id]
          file.write(sprintf("%d", passage))
          tile_id += 1
        end
        write_linebreak(file)
        file.write("\t],")
        write_linebreak(file)
        # Fin des passabilites
        
        
        # Altitudes
        file.write("\t\"altitude\": [")
        tile_id = 384
        while tilesetInfo.priorities[tile_id] != nil
          # virgule entre les valeurs
          if tile_id != 384
            file.write(", ")
          end
          # Retour a la ligne
          if tile_id % 8 == 0
            write_linebreak(file)
            file.write("\t\t")
          end
          # Ecriture de la valeur
          file.write(sprintf("%d", tilesetInfo.priorities[tile_id]))
          tile_id += 1
        end
        write_linebreak(file)
        file.write("\t],")
        write_linebreak(file)
        # Fin des altitudes

        # Terrain
        file.write("\t\"terrain\": [")
        tile_id = 384
        while tilesetInfo.terrain_tags[tile_id] != nil
          # Virgule entre les valeurs
          if tile_id != 384
            file.write(", ")
          end
          # Retour a la ligne
          if tile_id % 8 == 0
            write_linebreak(file)
            file.write("\t\t")
          end
          # Ecriture de la valeur
          file.write(sprintf("%d", tilesetInfo.terrain_tags[tile_id]))
          tile_id += 1
        end
        write_linebreak(file)
        file.write("\t],")
        write_linebreak(file)
        # Fin des terrains
        
        # Autotile
        file.write("\t\"autotiles\": [")
        write_linebreak(file)
        autotile_id = 0
        while tilesetInfo.autotile_names[autotile_id] != nil
          nom_autotile = tilesetInfo.autotile_names[autotile_id]
          if nom_autotile != ""
            if autotile_id != 0
              file.write(",")
              write_linebreak(file)
            end
            file.write("\t\t{")
            write_linebreak(file)
            
            file.write(sprintf("\t\t\t\"nomImage\": \"%s\",", nom_autotile))
            write_linebreak(file)
            numero_autotile = autotile_id - 8 #-1 est la case vide
            file.write(sprintf("\t\t\t\"numero\": %d,", numero_autotile))
            write_linebreak(file)
            passabilite_autotile = case tilesetInfo.passages[(autotile_id + 1) * 48]
              when 15 then 1
              else 0
            end
            file.write(sprintf("\t\t\t\"passabilite\": %d,", passabilite_autotile))
            write_linebreak(file)
            altitude_autotile = tilesetInfo.priorities[(autotile_id + 1) * 48]
            file.write(sprintf("\t\t\t\"altitude\": %d,", altitude_autotile))
            write_linebreak(file)
            terrain_autotile = tilesetInfo.terrain_tags[(autotile_id + 1) * 48]
            file.write(sprintf("\t\t\t\"terrain\": %d", terrain_autotile))
            write_linebreak(file)
            file.write("\t\t}")
          end
          autotile_id += 1
        end
        write_linebreak(file)
        file.write("\t],")
        write_linebreak(file)
        # Fin des autotiles
        
        # Panorama
        file.write(sprintf("\t\"panorama\": \"%s\",", tilesetInfo.panorama_name))
        write_linebreak(file)
        # TODO panorama_hue
        # ...
        # Fin du panorama
        
        # Brouillard
        file.write("\t\"brouillard\": {")
        write_linebreak(file)
        file.write(sprintf("\t\t\"nom\": \"%s\",", tilesetInfo.fog_name))
        write_linebreak(file)
        file.write(sprintf("\t\t\"opacite\": %d,", tilesetInfo.fog_opacity))
        write_linebreak(file)
        file.write(sprintf("\t\t\"defilementX\": %d,", tilesetInfo.fog_sx))
        write_linebreak(file)
        file.write(sprintf("\t\t\"defilementY\": %d,", tilesetInfo.fog_sy))
        write_linebreak(file)
        file.write(sprintf("\t\t\"zoom\": %d,", tilesetInfo.fog_zoom))
        write_linebreak(file)
        modeDeFusion = case tilesetInfo.fog_blend_type
          when 0 then "normal"
          when 1 then "negatif"
          when 2 then "addition"
        end
        file.write(sprintf("\t\t\"modeDeFusion\": \"%s\",", modeDeFusion))
        write_linebreak(file)
        file.write(sprintf("\t\t\"teinte\": %d", tilesetInfo.fog_hue))
        write_linebreak(file)
        file.write("\t}")
        write_linebreak(file)
        # Fin du brouillard

        # Fin de l'ecriture du tileset
        file.write("}")
        write_linebreak(file)
        
        # On referme le fichier du tileset
        file.close
      end
    end
    # Fin de l'exportation des Tilesets

    
    #------------------------
    # Exportation des objets
    #------------------------
    # On cree un fichier des objets
    filename = sprintf("%s/objets.json", EXPORT_MAIN_FOLDER)
    # On supprime le fichier s'il existe deja
    if File.exist?(filename)
      File.delete(filename)
    end
    # Ouvre le fichier et rajoute du texte a la fin
    file = File.new(filename, 'a')
    
    # On charge les data des objets
    items = load_data(ItemsFile)
    
    # Debut de l'ecriture des objets
    file.write("{")
    write_linebreak(file)
    file.write("\t\"objets\": [")
    items.each do |item|
      if item != nil
        if item.id != 1
          file.write(",")
        end
        write_linebreak(file)
        file.write("\t\t{")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\"idObjet\": %d,", item.id - 1))
        write_linebreak(file)
        $game_variables = Array.new(122) 
        $game_variables[121] = 0
        nom_fr = item.name
        $game_variables[121] = 1
        nom_en = item.name
        file.write(sprintf("\t\t\t\"nom\": [\"%s\", \"%s\"],", nom_fr, nom_en))
        write_linebreak(file)
        file.write(sprintf("\t\t\t\"nomIcone\": \"%s\",", item.icon_name))
        write_linebreak(file)
        $game_variables[121] = 0
        desc_fr = item.description
        $game_variables[121] = 1
        desc_en = item.description
        file.write(sprintf("\t\t\t\"description\": [\"%s\", \"%s\"],", desc_fr, desc_en))
        write_linebreak(file)
        file.write("\t\t\t\"effet\": [")
        write_linebreak(file)
        file.write("\t\t\t]")
        write_linebreak(file)
        file.write("\t\t}")
      end
    end
    write_linebreak(file)
    file.write("\t]")
    write_linebreak(file)
    file.write("}")
    write_linebreak(file)
    # Fin de l'exportation des objets
    
    
    #------------------------
    # Exportation des quetes
    #------------------------
    # On cree un fichier des quetes
    filename = sprintf("%s/quetes.json", EXPORT_MAIN_FOLDER)
    # On supprime le fichier s'il existe deja
    if File.exist?(filename)
      File.delete(filename)
    end
    # Ouvre le fichier et rajoute du texte a la fin
    file = File.new(filename, 'a')
    
    # On charge les data des quetes
    quetes = load_data(QuestsFile)
    
    # Debut de l'ecriture des quetes
    file.write("{")
    write_linebreak(file)
    file.write("\t\"quetes\": [")
    quetes.each do |quete|
      if quete != nil
        if quete.id % 2 == 1
          if quete.id != 1
            file.write(",")
          end
          write_linebreak(file)
          file.write("\t\t{")
          write_linebreak(file)
          file.write(sprintf("\t\t\t\"id\": %d,", (quete.id+1)/2-1))
          write_linebreak(file)
          $game_variables = Array.new(122) 
          $game_variables[121] = 0
          nom_fr = quete.name
          $game_variables[121] = 1
          nom_en = quete.name
          file.write(sprintf("\t\t\t\"nom\": [\"%s\", \"%s\"],", nom_fr, nom_en))
          write_linebreak(file)
          est_un_bonus = (quete.icon_name != "quete a faire icon")
          file.write(sprintf("\t\t\t\"bonus\": %s,", est_un_bonus))
          write_linebreak(file)
          $game_variables[121] = 0
          desc_fr = quete.description
          $game_variables[121] = 1
          desc_en = quete.description
          file.write(sprintf("\t\t\t\"description\": [\"%s\", \"%s\"],", desc_fr, desc_en))
          write_linebreak(file)
          file.write("\t\t\t\"numeroCarte\": 0,")
          write_linebreak(file)
          file.write("\t\t\t\"xCarte\": 320,")
          write_linebreak(file)
          file.write("\t\t\t\"yCarte\": 240")
          write_linebreak(file)
          file.write("\t\t}")
        end
      end
    end
    write_linebreak(file)
    file.write("\t]")
    write_linebreak(file)
    file.write("}")
    write_linebreak(file)
    # Fin de l'exportation des quetes
    
  end
  # Fin de la fonction principale
  
  
  #-----------------------------------------
  # Recuperer les commandes evenementielles
  #-----------------------------------------
  def write_commands(file, page)
    # S'il y a des commandes
    if page.list != nil && page.list.length > 0
      file.write("\t\t\t\"commandes\": [")
      write_linebreak(file)

      
      # Numerotation des choix
      nombre_de_choix_rencontres = 0
      choix_en_cours = Array.new
      
      # Numerotation des conditions
      nombre_de_conditions_rencontrees = 0
      conditions_en_cours = Array.new
      
      # Numerotation des boucles
      nombre_de_boucles_rencontrees = 0
      boucles_en_cours = Array.new
      
      
      # S'interesser aux event_data.code pour exporter d'autres types de commandes
      @command_id = 0
      il_y_a_deja_eu_une_commande_non_masquee = false
      masquer_la_commande = true
      while page.list[@command_id] != nil
        
        if !masquer_la_commande
          il_y_a_deja_eu_une_commande_non_masquee = true
        end
        
        commande = page.list[@command_id]
        
        # Faut-il masquer cette commande ?
        case commande.code 
        when 0, 224, 401, 408, 509, 655
          masquer_la_commande = true
        else
          # Masquer les suppressions de quetes
          if commande.code == 318 && commande.parameters[1] != 0 
            masquer_la_commande = true
          else
            masquer_la_commande = false
          end
        end
        
        # Accolade fermante de la commande precedente
        if @command_id != 0 && !masquer_la_commande && il_y_a_deja_eu_une_commande_non_masquee
          file.write("\t\t\t},")
          write_linebreak(file)
        end
        
        if !masquer_la_commande
          # Accolade ouvrante de la commande
          file.write("\t\t\t{")
          write_linebreak(file)
          
          case commande.code
          when 0
            # RM met des commandes de code 0 a chaque embranchement
          when 101
            # Message
            file.write("\t\t\t\t\"nom\": \"Message\",")
            write_linebreak(file)
            
            #recuperation du texte multiligne
            meme_message = true
            ligne_message = 0
            texte = ""
            while ligne_message < 4 && meme_message
              commande_suivante = page.list[@command_id+ligne_message]
              if ligne_message == 0 || commande_suivante.code == 401
                # La commande suivante concerne le meme message
                if ligne_message > 0
                  texte += "\\n"
                end
                texte += commande_suivante.parameters[0]
              else
                # La commande suivante ne concerne plus le meme message
                meme_message = false
              end
              ligne_message += 1
            end
            file.write(sprintf("\t\t\t\t\"texte\": %s", texte.inspect))
            write_linebreak(file)
            
          when 401
            # Message (autres lignes)
            
          when 102
            # Choix
            file.write("\t\t\t\t\"nom\": \"Choix\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"alternatives\": %s,", commande.parameters[0].inspect))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", nombre_de_choix_rencontres))
            write_linebreak(file)
            
            # Ajouter un nouvel element a la pile
            choix_en_cours << nombre_de_choix_rencontres
            nombre_de_choix_rencontres += 1
            
          when 402
            # Alternative du choix
            file.write("\t\t\t\t\"nom\": \"ChoixAlternative\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d,", choix_en_cours.last))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"alternative\": %d", commande.parameters[0]))
            write_linebreak(file)
            
          when 403
            # Annulation du choix
            file.write("\t\t\t\t\"nom\": \"ChoixAlternative\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d,", choix_en_cours.last))
            write_linebreak(file)
            file.write("\t\t\t\t\"alternative\": 4")
            write_linebreak(file)
            
          when 404
            # Fin du choix
            file.write("\t\t\t\t\"nom\": \"ChoixFin\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", choix_en_cours.last))
            write_linebreak(file)
            
            # Retirer le dernier element de la pile
            choix_en_cours.pop 
            
          when 103
            # Entrer un nombre
            file.write("\t\t\t\t\"nom\": \"EntrerUnNombre\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numeroDeVariable\": %d,", commande.parameters[0]))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"tailleDuNombre\": %d", commande.parameters[1]))
            write_linebreak(file)
            
          when 104
            # Modifier l'affichage des dialogues
            file.write("\t\t\t\t\"nom\": \"ModifierOptionsMessages\",")
            write_linebreak(file)
            position_message = case commande.parameters[0]
              when 0 then "haut"
              when 1 then "milieu"
              when 2 then "bas"
            end
            file.write(sprintf("\t\t\t\t\"position\": \"%s\",", position_message))
            write_linebreak(file)
            masquer_message = (commande.parameters[1] == 1)
            file.write(sprintf("\t\t\t\t\"masquer\": %s", masquer_message))
            write_linebreak(file)
            
          #when 105
            # Attendre l'appui sur une touche
            
          when 106
            # Attendre
            file.write("\t\t\t\t\"nom\": \"Attendre\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nombreDeFrames\": %d", commande.parameters[0]))
            write_linebreak(file)
            
          when 108
            # Commentaire
            file.write("\t\t\t\t\"nom\": \"Commentaire\",")
            write_linebreak(file)
            
            #recuperation du commentaire multiligne
            meme_commentaire = true
            ligne_commentaire = 0
            texte = ""
            while ligne_commentaire < 4 && meme_commentaire
              commande_suivante = page.list[@command_id+ligne_commentaire]
              if ligne_commentaire == 0 || commande_suivante.code == 408
                # La commande suivante concerne le meme commentaire
                if ligne_commentaire > 0
                  texte += "\\n"
                end
                texte += commande_suivante.parameters[0]
              else
                # La commande suivante ne concerne plus le meme message
                meme_commentaire = false
              end
              ligne_commentaire += 1
            end
            file.write(sprintf("\t\t\t\t\"texte\": %s", texte.inspect))
            write_linebreak(file)
            
          when 408
            # Commentaire (autres lignes)

          when 111
            # Condition si
            
            case commande.parameters[0]
            when 0
              # Condition interrupteur
              file.write("\t\t\t\t\"nom\": \"ConditionInterrupteur\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numeroInterrupteur\": %d,", commande.parameters[1]))
              write_linebreak(file)
              valeurQuIlEstCenseAvoir = (commande.parameters[1] == 0)
              file.write(sprintf("\t\t\t\t\"valeurQuIlEstCenseAvoir\": %s,", valeurQuIlEstCenseAvoir))
              write_linebreak(file)
            when 1
              # Condition variable
              file.write("\t\t\t\t\"nom\": \"ConditionVariable\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numeroVariable\": %d,", commande.parameters[1]))
              write_linebreak(file)
              inegalite = case commande.parameters[4]
                when 0 then "=="
                when 1 then ">="
                when 2 then "<="
                when 3 then ">"
                when 4 then "<"
                when 5 then "!="
              end
              file.write(sprintf("\t\t\t\t\"inegalite\": \"%s\",", inegalite))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"valeurQuIlEstCenseAvoir\": %s,", commande.parameters[3]))
              write_linebreak(file)
              comparer_a_une_autre_variable = (commande.parameters[2] == 1)
              file.write(sprintf("\t\t\t\t\"comparerAUneAutreVariable\": %s,", comparer_a_une_autre_variable))
              write_linebreak(file)
            when 2
              # Condition interrupteur local
              file.write("\t\t\t\t\"nom\": \"ConditionInterrupteurLocal\",")
              write_linebreak(file)
              numeroInterrupteurLocal = case commande.parameters[1]
                when "A" then 0
                when "B" then 1
                when "C" then 2
                when "D" then 3
              end
              file.write(sprintf("\t\t\t\t\"numeroInterrupteurLocal\": %d,", numeroInterrupteurLocal))
              write_linebreak(file)
              valeurQuIlEstCenseAvoir = (commande.parameters[2] == 0)
              file.write(sprintf("\t\t\t\t\"valeurQuIlEstCenseAvoir\": %s,", valeurQuIlEstCenseAvoir))
              write_linebreak(file)
            when 3
              # Condition chronometre
              file.write("\t\t\t\t\"nom\": \"ConditionChronometre\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"nombreDeSecondes\": %d,", commande.parameters[1]))
              write_linebreak(file)
              inegalite = case commande.parameters[2]
                when 0 then ">="
                when 1 then "<="
              end
              file.write(sprintf("\t\t\t\t\"inegalite\": \"%s\",", inegalite))
              write_linebreak(file)
            when 4
              # Conditions sur les coequipiers (parametre 1)
              case commande.parameters[2]
              when 2
                # Condition competence apprise
                file.write("\t\t\t\t\"nom\": \"ConditionEtatQuete\",")
                write_linebreak(file)
                idQuete = (commande.parameters[3]-1)/2
                file.write(sprintf("\t\t\t\t\"idQuete\": %d,", idQuete))
                write_linebreak(file)
                etatQuete = case commande.parameters[3]%2
                  when 0 then "FAITE"
                  when 1 then "CONNUE"
                end
                file.write(sprintf("\t\t\t\t\"etat\": \"%s\",", etatQuete))
                write_linebreak(file)
                
              when 3
                # Arme equipee
                file.write("\t\t\t\t\"nom\": \"ConditionArmeEquipee\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"idArme\": %d,", commande.parameters[3] - 1))
                write_linebreak(file)
                
              when 4
                # Armure equipee
                file.write("\t\t\t\t\"nom\": \"ConditionGadgetEquipe\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"idGadget\": %d,", commande.parameters[3] - 1))
                write_linebreak(file)
              end
              # Fin des conditions sur les coequipiers
              
            when 6
              # Condition direction
                file.write("\t\t\t\t\"nom\": \"ConditionDirection\",")
                write_linebreak(file)
                idEvent = commande.parameters[1]
                if idEvent == 0 #dans RM 0 est cet event
                  #dans java cet event est null
                  #donc ne rien ecrire
                else
                  if idEvent == -1 #dans RM -1 est le heros
                    idEvent = 0 #dans java le heros est 0
                  end
                  file.write(sprintf("\t\t\t\t\"idEventConcerne\": %d,", idEvent))
                  write_linebreak(file)
                end
                direction = case commande.parameters[2]
                  when 2 then 0
                  when 4 then 1
                  when 6 then 2
                  when 8 then 3
                end
                file.write(sprintf("\t\t\t\t\"direction\": %d,", direction))
                write_linebreak(file)
              
              when 7
                # Condition argent
                file.write("\t\t\t\t\"nom\": \"ConditionArgent\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"quantite\": %d,", commande.parameters[1]))
                write_linebreak(file)
                inegalite = case commande.parameters[2]
                  when 0 then ">="
                  when 1 then "<="
                end
                file.write(sprintf("\t\t\t\t\"inegalite\": \"%s\",", inegalite))
                write_linebreak(file)
              
              when 8
                # Condition objet possede
                file.write("\t\t\t\t\"nom\": \"ConditionObjetPossede\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"idObjet\": %d,", commande.parameters[1] - 1))
                write_linebreak(file)
                
              when 9
                # Condition arme possedee
                file.write("\t\t\t\t\"nom\": \"ConditionArmePossedee\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"idArme\": %d,", commande.parameters[1] - 1))
                write_linebreak(file)
                
              when 10
                # Condition armure possedee
                file.write("\t\t\t\t\"nom\": \"ConditionGadgetPossede\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"idGadget\": %d,", commande.parameters[1] - 1))
                write_linebreak(file)
                
              when 11
                # Condition touche
                file.write("\t\t\t\t\"nom\": \"ConditionTouche\",")
                write_linebreak(file)
                touche = case commande.parameters[1]
                  when 2 then "BAS"
                  when 4 then "GAUCHE"
                  when 6 then "DROITE"
                  when 8 then "HAUT"
                  when 11 then "ACTION_SECONDAIRE" # Z ou shift
                  when 12 then "MENU" # echap, X ou 0
                  when 13 then "ACTION" # entree ou espace
                  when 14 then "A"
                  when 15 then "S"
                  when 16 then "D"
                  when 17 then "ARME_SUIVANTE" #Q
                  when 18 then "ARME_PRECEDENTE" #W
                end
                file.write(sprintf("\t\t\t\t\"touche\": \"%s\",", touche))
                write_linebreak(file)
              
              when 12
                # Condition script
                file.write("\t\t\t\t\"nom\": \"ConditionScript\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"script\": %s,", commande.parameters[1].inspect))
                write_linebreak(file)
              
                # Pas d'autres types de Conditions
            end

            file.write(sprintf("\t\t\t\t\"numero\": %d", nombre_de_conditions_rencontrees))
            write_linebreak(file)
            
            conditions_en_cours << nombre_de_conditions_rencontrees
            nombre_de_conditions_rencontrees += 1
            # Voila pour les Conditions
            
            
          when 411
            # Sinon
            file.write("\t\t\t\t\"nom\": \"ConditionSinon\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", conditions_en_cours.last))
            write_linebreak(file)
            
          when 412
            # Fin de si
            file.write("\t\t\t\t\"nom\": \"ConditionFin\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", conditions_en_cours.last))
            write_linebreak(file)
            
            # On retire le dernier element de la liste
            conditions_en_cours.pop
            
          when 112
            # Boucle
            file.write("\t\t\t\t\"nom\": \"Boucle\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", nombre_de_boucles_rencontrees))
            write_linebreak(file)
            
            boucles_en_cours << nombre_de_boucles_rencontrees
            nombre_de_boucles_rencontrees += 1
            
          when 113
            # Sortir de la boucle
            file.write("\t\t\t\t\"nom\": \"BoucleSortir\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", boucles_en_cours.last))
            write_linebreak(file)
            
          when 413
            # Fin de boucle
            file.write("\t\t\t\t\"nom\": \"BoucleFin\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", boucles_en_cours.last))
            write_linebreak(file)
            
            # On retire le dernier element de la liste
            boucles_en_cours.pop
            
            
          when 115 
            # Arreter les events
            # ???

          when 116
            # Effacer l'event
            file.write("\t\t\t\t\"nom\": \"SupprimerEvent\"")
            write_linebreak(file)

          when 117
            # Appeler un code commun
            file.write("\t\t\t\t\"nom\": \"AppelerPageCommune\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numeroPageCommune\": %d", commande.parameters[0]))
            write_linebreak(file)
            
          when 118
            # Etiquette
            file.write("\t\t\t\t\"nom\": \"Etiquette\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nomEtiquette\": \"%s\"", commande.parameters[0]))
            write_linebreak(file)
            
          when 119
            # Aller vers l'etiquette
            file.write("\t\t\t\t\"nom\": \"AllerVersEtiquette\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nomEtiquette\": \"%s\"", commande.parameters[0]))
            write_linebreak(file)

          when 121
            # Modifier interrupteur
            file.write("\t\t\t\t\"nom\": \"ModifierInterrupteur\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numeroInterrupteur\": %d,", commande.parameters[0]))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"valeur\": %s", commande.parameters[2]==0))
            write_linebreak(file)

          when 122 
            # Modifier variable
            file.write("\t\t\t\t\"nom\": \"ModifierVariable\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numeroVariable\": %d,", commande.parameters[0]))
            write_linebreak(file)
            operation = case commande.parameters[2]
              when 0 then "rendre egal a"
              when 1 then "ajouter"
              when 2 then "retirer"
              when 3 then "multiplier"
              when 4 then "diviser"
              when 5 then "modulo"
            end
            
            # operation mathematique a effectuer
            file.write(sprintf("\t\t\t\t\"operationAFaire\": \"%s\",", operation))
            write_linebreak(file)
            
            operation2 = case commande.parameters[3]
              when 0 then "valeur brute"
              when 1 then "contenu de la variable"
              when 2 then "nombre aleatoire"
              when 3 then "objets possedes"
              when 6 then 
                case commande.parameters[5]
                  when 0 then "coordonnee x"
                  when 1 then "coordonnee y"
                  when 2 then "direction"
                  when 5 then "terrain"
                end
              when 7 then
                case commande.parameters[4]
                  when 0 then "numero de la map"
                  when 2 then "argent possede"
                end
            end
            
            if commande.parameters[3] != 7
              # pas de valeur a preciser pour l'argent et le numero de map
              if commande.parameters[3] == 3
                file.write(sprintf("\t\t\t\t\"valeurADonner\": %d,", commande.parameters[4]-1)) #id objet
              else
                file.write(sprintf("\t\t\t\t\"valeurADonner\": %d,", commande.parameters[4]))
              end
              write_linebreak(file)
            end
            
            if commande.parameters[3] == 2
              # un nombre aleatoire necessite une borne superieure
              file.write(sprintf("\t\t\t\t\"valeurADonner2\": %d,", commande.parameters[5]))
              write_linebreak(file)
            end
            # provenance de la valeur a assigner
            file.write(sprintf("\t\t\t\t\"operationAFaire2\": \"%s\"", operation2))
            write_linebreak(file)

          when 123
            # Modifier interrupteur local
            file.write("\t\t\t\t\"nom\": \"ModifierInterrupteurLocal\",")
            write_linebreak(file)
            numeroInterrupteur = case commande.parameters[0]
              when "A" then 0
              when "B" then 1
              when "C" then 2
              when "D" then 3
            end
            file.write(sprintf("\t\t\t\t\"numeroInterrupteurLocal\": %d,", numeroInterrupteur))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"valeurADonner\": %s", commande.parameters[1]==0))
            write_linebreak(file)
            
          when 124
            # Chronometre
            case commande.parameters[0]
              when 0
                # Demarrer
                file.write("\t\t\t\t\"nom\": \"DemarrerChronometre\",")
                write_linebreak(file)
                file.write(sprintf("\t\t\t\t\"depart\": %d", commande.parameters[1]))
                write_linebreak(file)
              when 1
                # Arreter
                file.write("\t\t\t\t\"nom\": \"ArreterChronometre\"")
                write_linebreak(file)              
            end

          when 125
            # Ajouter/retirer argent
            case commande.parameters[0]
            when 0
              # Ajout
              file.write("\t\t\t\t\"nom\": \"AjouterArgent\",")
              write_linebreak(file)
            when 1
              # Retrait
              file.write("\t\t\t\t\"nom\": \"RetirerArgent\",")
              write_linebreak(file)
            end
            file.write(sprintf("\t\t\t\t\"variable\": %s,", commande.parameters[1]==1))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"quantite\": %d", commande.parameters[2]))
            write_linebreak(file)
          
          when 126
            # Ajouter/retirer objet
            case commande.parameters[1]
            when 0
              # Ajout
              file.write("\t\t\t\t\"nom\": \"AjouterObjet\",")
              write_linebreak(file)
            when 1
              # Retrait
              file.write("\t\t\t\t\"nom\": \"RetirerObjet\",")
              write_linebreak(file)
            end
            file.write(sprintf("\t\t\t\t\"idObjet\": %d,", commande.parameters[0] - 1))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"variable\": %s,", commande.parameters[2]==1))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"quantite\": %d", commande.parameters[3]))
            write_linebreak(file)
            
          when 127
            # Ajouter/retirer arme
            case commande.parameters[1]
            when 0
              # Ajout
              file.write("\t\t\t\t\"nom\": \"AjouterArme\",")
              write_linebreak(file)
            when 1
              # Retrait
              file.write("\t\t\t\t\"nom\": \"RetirerArme\",")
              write_linebreak(file)
            end
            #file.write(sprintf("\t\t\t\t\"variable\": %s,", commande.parameters[2]==1))
            #write_linebreak(file)
            #file.write(sprintf("\t\t\t\t\"quantite\": %d,", commande.parameters[3]))
            #write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"idArme\": %d", commande.parameters[0] - 1))
            write_linebreak(file)
            
          when 128
            # Ajouter/retirer armure
            case commande.parameters[1]
            when 0
              # Ajout
              file.write("\t\t\t\t\"nom\": \"AjouterGadget\",")
              write_linebreak(file)
            when 1
              # Retrait
              file.write("\t\t\t\t\"nom\": \"RetirerGadget\",")
              write_linebreak(file)
            end
            #file.write(sprintf("\t\t\t\t\"variable\": %s,", commande.parameters[2]==1))
            #write_linebreak(file)
            #file.write(sprintf("\t\t\t\t\"quantite\": %d,", commande.parameters[3]))
            #write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"idGadget\": %d", commande.parameters[0] - 1))
            write_linebreak(file)
            
          #When 129 # Swap actors
          #When 131 # Changer l'apparence de la boite de dialogues
          #When 132 # Change Battle BGM
          #When 133 # Change battle end ME
          #when 134 # Autoriser sauvegarde
            
          when 135
            # Autoriser menu
            file.write("\t\t\t\t\"nom\": \"AutoriserMenu\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"autoriser\": %s", commande.parameters[0]==1))
            write_linebreak(file)
            
          #When 136 # Activer les combats aleatoires
                      
          when 201
            # Changer de Map
            file.write("\t\t\t\t\"nom\": \"ChangerDeMap\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"variable\": %s,", commande.parameters[0]==1))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numeroNouvelleMap\": %d,", commande.parameters[1]))
            write_linebreak(file)
            if commande.parameters[4] != 0
              direction = case commande.parameters[4]
                when 2 then 0
                when 4 then 1
                when 6 then 2
                when 8 then 3
              end
              file.write(sprintf("\t\t\t\t\"directionDebutHeros\": %d,", direction))
              write_linebreak(file)
            end
            file.write(sprintf("\t\t\t\t\"xDebutHeros\": %d,", commande.parameters[2]))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"yDebutHeros\": %d", commande.parameters[3]))
            write_linebreak(file)
            
          when 202
            # Repositionner un Event
            file.write("\t\t\t\t\"nom\": \"TeleporterEvent\",")
            write_linebreak(file)
            if commande.parameters[0] != 0 # 0 signifie "cet event"
              file.write(sprintf("\t\t\t\t\"idEvent\": %d,", commande.parameters[0]))
              write_linebreak(file)
            end
            file.write(sprintf("\t\t\t\t\"variable\": %s,", commande.parameters[1]==1))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nouveauX\": %d,", commande.parameters[2]))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nouveauY\": %d,", commande.parameters[3]))
            write_linebreak(file)
            direction = case commande.parameters[4]
              when 2 then 0
              when 4 then 1
              when 6 then 2
              when 8 then 3
            end
            file.write(sprintf("\t\t\t\t\"direction\": %d", direction))
            write_linebreak(file)
          
          when 203 
            # Faire defiler la map
            file.write("\t\t\t\t\"nom\": \"FaireDefilerLaMap\",")
            write_linebreak(file)
            direction = case commande.parameters[0]
              when 2 then 0
              when 4 then 1
              when 6 then 2
              when 8 then 3
            end
            file.write(sprintf("\t\t\t\t\"direction\": %d,", direction))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nombreDeCarreaux\": %d,", commande.parameters[1]))
            write_linebreak(file)
            vitesse = case commande.parameters[2]
              when 0 then 1
              when 1 then 2
              when 2 then 4
              when 3 then 8
              when 4 then 12
              when 5 then 16
              when 6 then 24
            end
            file.write(sprintf("\t\t\t\t\"vitesse\": %d", vitesse))
            write_linebreak(file)

          when 204
            case commande.parameters[0]
            when 0
              # Panorama
              file.write("\t\t\t\t\"nom\": \"ModifierPanorama\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"nuance\": %d,", commande.parameters[2]))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"nomImage\": \"%s\"", commande.parameters[1]))
              write_linebreak(file)
              
            when 1
              # Brouillard
              file.write("\t\t\t\t\"nom\": \"ModifierBrouillard\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"opacite\": %d,", commande.parameters[3]))
              write_linebreak(file)
              modeDeFusion = case commande.parameters[4]
                when 0 then "normal"
                when 1 then "negatif"
                when 2 then "addition"
              end
              file.write(sprintf("\t\t\t\t\"modeDeFusion\": \"%s\",", modeDeFusion))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"defilementX\": %d,", commande.parameters[6]))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"defilementY\": %d,", commande.parameters[7]))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"zoom\": %d,", commande.parameters[5]))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"nuance\": %d,", commande.parameters[2]))
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"nomImage\": \"%s\"", commande.parameters[1]))
              write_linebreak(file)
            end
            
          when 205
            # Ton du brouillard
            file.write("\t\t\t\t\"nom\": \"ModifierBrouillard\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"rouge\": %d,", (commande.parameters[0].red+255)/2 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"vert\": %d,", (commande.parameters[0].green+255)/2 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"bleu\": %d,", (commande.parameters[0].blue+255)/2 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"gris\": %d,", commande.parameters[0].gray ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"dureeTransition\": %d", commande.parameters[1]))
            write_linebreak(file)
            
          when 206
            # Opacite du brouillard
            file.write("\t\t\t\t\"nom\": \"ModifierBrouillard\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"opacite\": %d,", commande.parameters[0] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"dureeTransition\": %d", commande.parameters[1] ))
            write_linebreak(file)
            
          when 207 
            # Animation
            file.write("\t\t\t\t\"nom\": \"JouerAnimation\",")
            write_linebreak(file)
            # event cible de l'animation
            case commande.parameters[0]
              when -1
                # heros
                file.write("\t\t\t\t\"idEvent\": 0,")
                write_linebreak(file)
              when 0
                # cet event
              else
                # un event
                file.write(sprintf("\t\t\t\t\"idEvent\": %d,", commande.parameters[0]))
                write_linebreak(file)
            end
            file.write(sprintf("\t\t\t\t\"idAnimation\": %d", commande.parameters[1]))
            write_linebreak(file)
            
          when 209
            # Deplacement
            file.write("\t\t\t\t\"nom\": \"Deplacement\",")
            write_linebreak(file)
            # event e deplacer
            case commande.parameters[0]
              when -1
                # heros
                file.write("\t\t\t\t\"idEventADeplacer\": 0,")
                write_linebreak(file)
              when 0
                # cet event
              else
                # un event
                file.write(sprintf("\t\t\t\t\"idEventADeplacer\": %d,", commande.parameters[0]))
                write_linebreak(file)
            end
            facultatif = commande.parameters[1].skippable
            file.write(sprintf("\t\t\t\t\"ignorerLesMouvementsImpossibles\": %s,", facultatif))
            write_linebreak(file)
            repeter = commande.parameters[1].repeat
            file.write(sprintf("\t\t\t\t\"repeterLeDeplacement\": %s,", repeter))
            write_linebreak(file)
            file.write("\t\t\t\t\"attendreLaFinDuDeplacement\": false,")
            write_linebreak(file)
            file.write("\t\t\t\t\"mouvements\": [")
            write_linebreak(file)
            
            i_mouvement = 1
            commande_suivante = page.list[@command_id + i_mouvement]
            while commande_suivante != nil && commande_suivante.code == 509
              # Mouvements
              mouvement = commande_suivante.parameters[0]
              ecrire_mouvement(mouvement, file)
              
              i_mouvement += 1
              commande_suivante = page.list[@command_id + i_mouvement]
              
              if commande_suivante != nil && commande_suivante.code == 509
                file.write(",")
              end
              write_linebreak(file)
              
            end
            file.write("\t\t\t\t]")
            write_linebreak(file)
          
          when 509
            # Mouvements du deplacement
            
          when 210 
            # Attendre la fin du deplacement
            file.write("\t\t\t\t\"nom\": \"AttendreLaFinDesDeplacements\",")
            write_linebreak(file)
            file.write("\t\t\t\t\"idEvent\": -1")
            write_linebreak(file)
            
          #when 221 # Transition preparation  
          #when 222 # Transition execution 
            
          when 223
            # Ton de l'ecran
            file.write("\t\t\t\t\"nom\": \"ModifierTonDeLEcran\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"rouge\": %d,", (commande.parameters[0].red+255)/2 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"vert\": %d,", (commande.parameters[0].green+255)/2 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"bleu\": %d,", (commande.parameters[0].blue+255)/2 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"gris\": %d,", commande.parameters[0].gray ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"dureeTransition\": %d", commande.parameters[1]))
            write_linebreak(file)
            
          when 224
            # Flasher l'ecran
            
          when 225
            # Tremblement de terre
            file.write("\t\t\t\t\"nom\": \"TremblementDeTerre\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"intensite\": %d,", commande.parameters[0] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"vitesse\": %d,", commande.parameters[1] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nombreDeFrames\": %d", commande.parameters[2] ))
            write_linebreak(file)
            
          when 231
            # Afficher image
            file.write("\t\t\t\t\"nom\": \"AfficherImage\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d,", commande.parameters[0] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nomImage\": \"%s\",", commande.parameters[1] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"centre\": %s,", commande.parameters[2]==1 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"variables\": %s,", commande.parameters[3]==1 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"x\": %d,", commande.parameters[4] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"y\": %d,", commande.parameters[5] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"zoomX\": %d,", commande.parameters[6] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"zoomY\": %d,", commande.parameters[7] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"opacite\": %d,", commande.parameters[8] ))
            write_linebreak(file)
            modeDeFusion = case commande.parameters[9]
              when 0 then "normal"
              when 1 then "addition"
              when 2 then "soustraction"
            end
            file.write(sprintf("\t\t\t\t\"modeDeFusion\": \"%s\"", modeDeFusion ))
            write_linebreak(file)
            
          when 232
            # Deplacer image
            file.write("\t\t\t\t\"nom\": \"DeplacerImage\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d,", commande.parameters[0] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nombreDeFrames\": %d,", commande.parameters[1] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"centre\": %s,", commande.parameters[2]==1 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"variables\": %s,", commande.parameters[3]==1 ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"x\": %d,", commande.parameters[4] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"y\": %d,", commande.parameters[5] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"zoomX\": %d,", commande.parameters[6] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"zoomY\": %d,", commande.parameters[7] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"opacite\": %d,", commande.parameters[8] ))
            write_linebreak(file)
            modeDeFusion = case commande.parameters[9]
              when 0 then "normal"
              when 1 then "addition"
              when 2 then "soustraction"
            end
            file.write(sprintf("\t\t\t\t\"modeDeFusion\": \"%s\"", modeDeFusion ))
            write_linebreak(file)
          
          when 233
            # Faire tourner image
            file.write("\t\t\t\t\"nom\": \"DeplacerImage\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d,", commande.parameters[0] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"angle\": %d,", commande.parameters[1] ))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nombreDeFrames\": %d,", 2 ))
            write_linebreak(file)
            file.write("\t\t\t\t\"repeterLeDeplacement\": true")
            write_linebreak(file)
          
          when 235
            # Effacer une image
            file.write("\t\t\t\t\"nom\": \"EffacerImage\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"numero\": %d", commande.parameters[0] ))
            write_linebreak(file)
            
          when 236
            # Meteo
            file.write("\t\t\t\t\"nom\": \"ModifierMeteo\",")
            write_linebreak(file)
            typeMeteo = case commande.parameters[0]
              when 0 then "rien"
              when 1 then "pluie"
              when 2 then "pluie"
              when 3 then "neige"
            end
            file.write(sprintf("\t\t\t\t\"type\": \"%s\",", typeMeteo))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"intensite\": %d", commande.parameters[1]))
            write_linebreak(file)
            
          when 241
            # BGM
            file.write("\t\t\t\t\"nom\": \"JouerMusique\",")
            write_linebreak(file)
            nomFichier = commande.parameters[0].name
            extension = File.extname(Dir.entries("./Audio/BGM").select{|s| s.index(nomFichier+'.') == 0}.first)
            file.write(sprintf("\t\t\t\t\"nomFichierSonore\": \"%s\",", nomFichier+extension))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"volume\": %.2f,", commande.parameters[0].volume.to_f/100))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"tempo\": %.2f", commande.parameters[0].pitch.to_f/100))
            write_linebreak(file)
          
          when 242
            # Fondu BGM
            file.write("\t\t\t\t\"nom\": \"ArreterMusique\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nombreDeFrames\": %d", commande.parameters[0] * 30 ))
            write_linebreak(file)
            
          when 245
            # BGS
            file.write("\t\t\t\t\"nom\": \"JouerFondSonore\",")
            write_linebreak(file)
            nomFichier = commande.parameters[0].name
            extension = File.extname(Dir.entries("./Audio/BGS").select{|s| s.index(nomFichier+'.') == 0}.first)
            file.write(sprintf("\t\t\t\t\"nomFichierSonore\": \"%s\",", nomFichier+extension))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"volume\": %.2f,", commande.parameters[0].volume.to_f/100))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"tempo\": %.2f", commande.parameters[0].pitch.to_f/100))
            write_linebreak(file)
            
          when 246
            # Fondu BGS
            file.write("\t\t\t\t\"nom\": \"ArreterFondSonore\",")
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"nombreDeFrames\": %d", commande.parameters[0] * 30 ))
            write_linebreak(file)
          
          when 249
            # ME
            file.write("\t\t\t\t\"nom\": \"JouerEffetMusical\",")
            write_linebreak(file)
            nomFichier = commande.parameters[0].name
            extension = File.extname(Dir.entries("./Audio/ME").select{|s| s.index(nomFichier+'.') == 0}.first)
            file.write(sprintf("\t\t\t\t\"nomFichierSonore\": \"%s\",", nomFichier+extension))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"volume\": %.2f,", commande.parameters[0].volume.to_f/100))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"tempo\": %.2f", commande.parameters[0].pitch.to_f/100))
            write_linebreak(file)
            
          when 250
            # SE
            file.write("\t\t\t\t\"nom\": \"JouerEffetSonore\",")
            write_linebreak(file)
            nomFichier = commande.parameters[0].name
            extension = File.extname(Dir.entries("./Audio/SE").select{|s| s.index(nomFichier+'.') == 0}.first)
            file.write(sprintf("\t\t\t\t\"nomFichierSonore\": \"%s\",", nomFichier+extension))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"volume\": %.2f,", commande.parameters[0].volume.to_f/100))
            write_linebreak(file)
            file.write(sprintf("\t\t\t\t\"tempo\": %.2f", commande.parameters[0].pitch.to_f/100))
            write_linebreak(file)
            
          when 251
            # Arreter les SE
            file.write("\t\t\t\t\"nom\": \"ArreterLesEffetsSonores\"")
            write_linebreak(file)
            
          when 303
            # Rentrer un mot
            file.write("\t\t\t\t\"nom\": \"OuvrirMenu\",")
            write_linebreak(file)
            file.write("\t\t\t\t\"nomMenu\": \"MotDePasse\"")
            write_linebreak(file)
            
          when 318
            # Avancement de la quete
            if commande.parameters[1] == 0 
              file.write("\t\t\t\t\"nom\": \"ModifierAvancementQuete\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"numero\": %d,", (commande.parameters[2]+1)/2 ))
              write_linebreak(file)
              if commande.parameters[2] %2 == 0 
                avancement = "FAITE"
              else
                avancement = "CONNUE"
              end
              file.write(sprintf("\t\t\t\t\"avancement\": \"%s\"", avancement))
              write_linebreak(file)
            end
            
          when 319
            # Equipement
            if commande.parameters[1] == 0 # Arme
              file.write("\t\t\t\t\"nom\": \"EquiperArme\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"idArme\": %d", commande.parameters[2]-1))
              write_linebreak(file)
            else # Gadget
              file.write("\t\t\t\t\"nom\": \"EquiperGadget\",")
              write_linebreak(file)
              file.write(sprintf("\t\t\t\t\"idGadget\": %d", commande.parameters[2]-1))
              write_linebreak(file)
            end
          
          when 351
            # Ouvrir le menu
            file.write("\t\t\t\t\"nom\": \"OuvrirMenu\",")
            write_linebreak(file)
            file.write("\t\t\t\t\"nomMenu\": \"Statut\"")
            write_linebreak(file)
          
          when 352
            # Ouvrir le menu de sauvegarde
            file.write("\t\t\t\t\"nom\": \"OuvrirMenu\",")
            write_linebreak(file)
            file.write("\t\t\t\t\"nomMenu\": \"SauvegarderPartie\"")
            write_linebreak(file)
            
          when 353
            # Ouvrir le menu de sauvegarde
            file.write("\t\t\t\t\"nom\": \"OuvrirMenu\",")
            write_linebreak(file)
            file.write("\t\t\t\t\"nomMenu\": \"GameOver\"")
            write_linebreak(file)
            
          when 354
            # Ouvrir le menu de sauvegarde
            file.write("\t\t\t\t\"nom\": \"OuvrirMenu\",")
            write_linebreak(file)
            file.write("\t\t\t\t\"nomMenu\": \"Titre\"")
            write_linebreak(file)

          when 355
            # Script
            file.write("\t\t\t\t\"nom\": \"AppelerUnScript\",")
            write_linebreak(file)
            
            script_total = commande.parameters[0]
            
            i_ligne_script = 1
            autre_ligne_script = page.list[@command_id + i_ligne_script]
            meme_script = (autre_ligne_script.code == 655)
            while meme_script
              script_total += "\\n"
              script_total += autre_ligne_script.parameters[0]
              
              i_ligne_script += 1
              autre_ligne_script = page.list[@command_id + i_ligne_script]
              meme_script = (autre_ligne_script.code == 655)
            end
            
            file.write(sprintf("\t\t\t\t\"script\": %s", script_total.inspect))
            write_linebreak(file)
            
          when 655
          # Autres lignes de script
          
          else
            # Commande inconnue
            file.write(sprintf("\t\t\t\t\"nom\": \"%d\",", commande.code))
            write_linebreak(file)
            
            # Parametres de la commande inconnue
            for parameter_id in 0...commande.parameters.size
              parametre = commande.parameters[parameter_id].to_s
              file.write(sprintf("\t\t\t\t\"%d\": \"%s\"", parameter_id, parametre))
              
              if parameter_id < commande.parameters.size - 1
                file.write(",")
              end
              write_linebreak(file)
            end
            # Fin commande inconnue
          end
        end
      
        @command_id += 1
      end
      
      # Derniere accolade des commandes
      if il_y_a_deja_eu_une_commande_non_masquee
        file.write("\t\t\t}")
        write_linebreak(file)
      end
      
      file.write("\t\t\t],")
      write_linebreak(file)
    end
  end
  

  #----------------------------------------------------------
  # Traduction du tile_id de RM vers celui de Kujira no Java
  #----------------------------------------------------------
  def normaliser_tile_id(tile_id)
    tile_id = tile_id - 384 # On decale les autotiles dans les negatifs
    if tile_id < 0
      # RPG Maker differencie les 48 apparences possibles d'un autotile
      tile_id = tile_id/48 - 1
      # On decale les autotiles pour que -1 represente la case vide
      if tile_id == -9
        tile_id = -1
      end
    end
    return tile_id
  end
  
  
  #---------------------
  # Ecrire un mouvement
  #---------------------
  def ecrire_mouvement(mouvement, file)
    file.write("\t\t\t\t{")
    write_linebreak(file)
    case mouvement.code 
      when 1  # marche vers le bas
        file.write("\t\t\t\t\t\"nom\": \"Avancer\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 0,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 2  # marche vers la gauche
        file.write("\t\t\t\t\t\"nom\": \"Avancer\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 1,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 3  # marche vers la droite
        file.write("\t\t\t\t\t\"nom\": \"Avancer\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 2,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 4  # marche vers le haut
        file.write("\t\t\t\t\t\"nom\": \"Avancer\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 3,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 5  # marche gauche bas
        file.write("\t\t\t\t\t\"nom\": \"PasEnDiagonale\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionVerticale\": 0,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionHorizontale\": 1,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 6  # marche droite bas
        file.write("\t\t\t\t\t\"nom\": \"PasEnDiagonale\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionVerticale\": 0,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionHorizontale\": 2,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 7  # marche gauche haut
        file.write("\t\t\t\t\t\"nom\": \"PasEnDiagonale\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionVerticale\": 3,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionHorizontale\": 1,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 8  # marche droite haut
        file.write("\t\t\t\t\t\"nom\": \"PasEnDiagonale\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionVerticale\": 3,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionHorizontale\": 2,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nombreDeCarreaux\": 1")
      when 9  # marche aleatoire
        file.write("\t\t\t\t\t\"nom\": \"AvancerAleatoirement\"")
      when 10  # suivre heros
        file.write("\t\t\t\t\t\"nom\": \"AvancerEnFonctionDUnEvent\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"idEventObserve\": 0,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"sens\": \"suivre\"")
      when 11  # fuir heros
        file.write("\t\t\t\t\t\"nom\": \"AvancerEnFonctionDUnEvent\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"idEventObserve\": 0,")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"sens\": \"fuir\"")
      when 12  # marche avant
        file.write("\t\t\t\t\t\"nom\": \"PasEnAvant\"")
      when 13  # marche arriere
        file.write("\t\t\t\t\t\"nom\": \"PasEnArriere\"")
      when 14  # saut
        file.write("\t\t\t\t\t\"nom\": \"Sauter\",")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"x\": %d,", mouvement.parameters[0]))
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"y\": %d", mouvement.parameters[1]))
      when 15  # attendre
        file.write("\t\t\t\t\t\"nom\": \"AppelerUneCommande\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nomCommande\": \"Attendre\",")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"nombreDeFrames\": %d", mouvement.parameters[0]))
      when 16  # regarder vers le bas
        file.write("\t\t\t\t\t\"nom\": \"RegarderDansUneDirection\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 0")
      when 17  # regarder vers la gauche
        file.write("\t\t\t\t\t\"nom\": \"RegarderDansUneDirection\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 1")
      when 18  # regarder vers la droite
        file.write("\t\t\t\t\t\"nom\": \"RegarderDansUneDirection\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 2")
      when 19  # regarder vers le haut
        file.write("\t\t\t\t\t\"nom\": \"RegarderDansUneDirection\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"direction\": 3")
      when 20  # pivoter de 90 degres horaires
        file.write("\t\t\t\t\t\"nom\": \"Pivoter\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"angle\": +90")
      when 21  # pivoter de 90 degres anti-horaires
        file.write("\t\t\t\t\t\"nom\": \"Pivoter\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"angle\": -90")
      when 22  # se retourner
        file.write("\t\t\t\t\t\"nom\": \"Pivoter\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"angle\": 180")
      when 23  # pivoter aleatoirement de 90 degres
        file.write("\t\t\t\t\t\"nom\": \"PivoterAleatoirement\"")
      when 24  # regarder dans une direction aleatoire
        file.write("\t\t\t\t\t\"nom\": \"RegarderDansUneDirectionAleatoire\"")
      when 25  # se tourner vers le heros
        file.write("\t\t\t\t\t\"nom\": \"RegarderUnEvent\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"idEvent\": 0")
      when 26  # se detourner du heros
        file.write("\t\t\t\t\t\"nom\": \"RegarderUnEvent\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"sens\": \"fuir\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"idEvent\": 0")
      when 27 # activer interrupteur
        file.write("\t\t\t\t\t\"nom\": \"AppelerUneCommande\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nomCommande\": \"ModifierInterrupteur\",")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"numeroInterrupteur\": %d,", mouvement.parameters[0]))
        write_linebreak(file)
        file.write("\t\t\t\t\t\"valeur\": true")
      when 28 # desactiver interrupteur
        file.write("\t\t\t\t\t\"nom\": \"AppelerUneCommande\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nomCommande\": \"ModifierInterrupteur\",")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"numeroInterrupteur\": %d,", mouvement.parameters[0]))
        write_linebreak(file)
        file.write("\t\t\t\t\t\"valeur\": false")
      when 29 # modifier vitesse
        file.write("\t\t\t\t\t\"nom\": \"ModifierVitesse\",")
        write_linebreak(file)
        vitesse = case mouvement.parameters[0]
          when 0 then 1
          when 1 then 2
          when 2 then 4
          when 3 then 8
          when 4 then 12
          when 5 then 16
          else        24
        end
        file.write(sprintf("\t\t\t\t\t\"vitesse\": %d,", vitesse))
      when 30 # modifier frequence
        file.write("\t\t\t\t\t\"nom\": \"ModifierFrequence\",")
        write_linebreak(file)
        frequence = case mouvement.parameters[0]
          when 0 then 32
          when 1 then 24
          when 2 then 16
          when 3 then 8
          when 4 then 4
          when 5 then 2
          else        1
        end
        file.write(sprintf("\t\t\t\t\t\"frequence\": %d,", frequence))
      when 31 # marche animee
        file.write("\t\t\t\t\t\"nom\": \"RendreAnimeEnMouvement\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"animeEnMouvement\": true")
      when 32 # marche non-animee
        file.write("\t\t\t\t\t\"nom\": \"RendreAnimeEnMouvement\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"animeEnMouvement\": false")
      when 33 # arret anime
        file.write("\t\t\t\t\t\"nom\": \"RendreAnimeALArret\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"animeALArret\": true")
      when 34 # arret non-anime
        file.write("\t\t\t\t\t\"nom\": \"RendreAnimeALArret\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"animeALArret\": false")
      when 35 # direction fixe
        file.write("\t\t\t\t\t\"nom\": \"RendreDirectionFixe\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionFixe\": true")
      when 36 # direction non-fixe
        file.write("\t\t\t\t\t\"nom\": \"RendreDirectionFixe\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"directionFixe\": false")
      when 37 # traversable
        file.write("\t\t\t\t\t\"nom\": \"RendreTraversable\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"traversable\": true")
      when 38 # non-traversable
        file.write("\t\t\t\t\t\"nom\": \"RendreTraversable\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"traversable\": false")
      when 39 # au dessus de tout
        file.write("\t\t\t\t\t\"nom\": \"RendreAuDessusDeTout\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"auDessusDeTout\": true")
      when 40 # pas au dessus de tout
        file.write("\t\t\t\t\t\"nom\": \"RendreAuDessusDeTout\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"auDessusDeTout\": false")
      when 41 # apparence
        file.write("\t\t\t\t\t\"nom\": \"ModifierApparence\",")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"image\": \"%s\",", mouvement.parameters[0]))
        write_linebreak(file)
        direction = case mouvement.parameters[2]
          when 2 then 0
          when 4 then 1
          when 6 then 2
          when 8 then 3
        end
        file.write(sprintf("\t\t\t\t\t\"direction\": %d,", direction))
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"animation\": %d,", mouvement.parameters[3]))
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"teinte\": %d", mouvement.parameters[1]))
      when 42 # opacite
        file.write("\t\t\t\t\t\"nom\": \"ModifierOpacite\",")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"opacite\": %d", mouvement.parameters[0]))
      when 43 # mode de fusion
        file.write("\t\t\t\t\t\"nom\": \"ModifierModeDeFusion\",")
        write_linebreak(file)
        modeDeFusion = case mouvement.parameters[0]
          when 0 then "normal"
          when 1 then "addition"
          when 2 then "soustraction"
        end
        file.write(sprintf("\t\t\t\t\t\"modeDeFusion\": \"%s\"", modeDeFusion))
      when 44 # jouer son
        file.write("\t\t\t\t\t\"nom\": \"AppelerUneCommande\",")
        write_linebreak(file)
        file.write("\t\t\t\t\t\"nomCommande\": \"JouerEffetSonore\",")
        write_linebreak(file)
        nomFichier = mouvement.parameters[0].name
        extension = File.extname(Dir.entries("./Audio/SE").select{|s| s.index(nomFichier+'.') == 0}.first)
        file.write(sprintf("\t\t\t\t\t\"nomFichierSonore\": \"%s\",", nomFichier+extension))
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"volume\": %.2f,", mouvement.parameters[0].volume.to_f/100))
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"tempo\": %.2f", mouvement.parameters[0].pitch.to_f/100))
      when 45 # script
        file.write("\t\t\t\t\t\"nom\": \"AppelerUnScript\",")
        write_linebreak(file)
        file.write(sprintf("\t\t\t\t\t\"script\": %s", mouvement.parameters[0].inspect))
      else
        file.write(sprintf("\t\t\t\t\t\"nom\": %s", mouvement.code))
    end
    write_linebreak(file)
    file.write("\t\t\t\t}")
  end
  
  
  # Fin de la classe
end