#------------------------------------------------------------------
# Dans un premier temps on crée une méthode string? 
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
  EXPORT_FOLDER = "./Exportation/Maps"
  
  #------------------------------------------------------------------
  # Ecrit un retour à la ligne
  #------------------------------------------------------------------
  def write_linebreak(file)
    file.write("\r\n")
  end
  
  #----------------------------------------------------
  # Fonction principale, elle exporte les maps en json
  #----------------------------------------------------
  def exporter
    maps = load_data(MapsFile)
    tilesets = load_data(TilesetsFile)
    
    # Si le dossier d'exportation n'existe pas, on le crée
    if !File.directory?(EXPORT_FOLDER)
      Dir.mkdir(EXPORT_FOLDER)
    end
    
    # On parcourt toutes les maps
    for map_id in 459...maps.size+1
      mapFile = sprintf("Data/Map%03d.rxdata", map_id)
      map = load_data(mapFile) rescue next
      
      mapInfo = maps[map_id]
      
      
      # On crée un fichier par map
      filename = sprintf("%s/%03d_%s.txt", EXPORT_FOLDER, map_id, mapInfo.name)
      # On supprime le fichier s'il existe déjà
      if File.exist?(filename)
        File.delete(filename)
      end
      # Ouvre le fichier et rajoute du texte à la fin
      file = File.new(filename, 'a') 
      
      # Début de l'écriture de la map
      file.write("{")
      write_linebreak(file)
      
      # Nom de la map
      file.write(sprintf("\t\"nom\": \"%s\",", mapInfo.name))
      write_linebreak(file)
      
      # Tileset de la map
      file.write(sprintf("\t\"tileset\": \"%s\",", tilesets[map.tileset_id].tileset_name))
      write_linebreak(file)
      
      # Largeur de la map
      file.write(sprintf("\t\"largeur\": %s,", map.width))
      write_linebreak(file)
      # Hauteur de la map
      file.write(sprintf("\t\"hauteur\": %s,", map.height))
      write_linebreak(file)
      
      # Musique de la map
      file.write(sprintf("\t\"bgm\": \"%s\",", map.bgm.name))
      write_linebreak(file)
      file.write(sprintf("\t\"bgs\": \"%s\",", map.bgs.name))
      write_linebreak(file)
      
      # Couches de décor
      for couche in [0, 1, 2]
        file.write(sprintf("\t\"couche%d\": [", couche))
        write_linebreak(file)
        for y in 0...map.height
          ligne = "\t["
          for x in 0...map.width
            # On récupère la case
            tile_id = normaliser_tile_id( map.data[x, y, couche] )
            ligne += sprintf("%04d", tile_id)
            # La dernière colonne n'a pas de virgule
            if x < map.width - 1
              ligne += ","
            end
          end
          ligne += "]"
          # La dernière ligne n'a pas de virgule
          if y < map.height - 1
            ligne += ","
          end
          file.write(ligne)
          write_linebreak(file)
        end
        file.write("\t],")
        write_linebreak(file)
      end
      # Fin des couches de décor
      
      file.write("\t\"events\": [")
      write_linebreak(file)
      # On parcourt tous les events
      for event_id in map.events.keys
        event = map.events[event_id]
        
        # Début de l'écriture de l'event
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
        
        
        # Un event générique n'a pas de pages
        if !event.name.include? "Clone"
          file.write(",")
          write_linebreak(file)
          
          file.write("\t\t\"pages\": [")
          write_linebreak(file)
          # On parcourt toutes les pages
          for i in 0...event.pages.size

            # Début de l'écriture de la page
            file.write("\t\t{")
            write_linebreak(file)          
            
            page = event.pages[i]
            
            
            # Ecriture des conditions de la page
            file.write("\t\t\t\"conditions\": [")
            write_linebreak(file)
            
            # Condition 1 (interrupteur)
            c = page.condition
            if c.switch1_valid
              
              file.write("\t\t\t{")
              write_linebreak(file)
              file.write("\t\t\t\t\"name\": \"Interrupteur\",")
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
              file.write("\t\t\t\t\"name\": \"Interrupteur\",")
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
              file.write("\t\t\t\t\"name\": \"Variable\",")
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
              file.write("\t\t\t\t\"name\": \"InterrupteurLocal\",")
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
            
            # Conditions déclencheuses
            #@trigger = @page.trigger
            # TODO
            #...

            file.write("\t\t\t],")
            write_linebreak(file)        
            # Fin des conditions de la page
            
            
            # Ecriture des commandes de la page
            write_commands(file, page)
            # Fin des commandes de la page
            
            
            # Ecriture des mouvements de la page
            if page.move_type != 0
              file.write("\t\t\t\"mouvements\": [")
              write_linebreak(file)
              file.write("\t\t\t{")
              write_linebreak(file)
              case page.move_type
              when 1
                # Aléatoire
                file.write("\t\t\t\t\"nom\": \"AvancerAleatoirement\"")
              when 2
                # Suit le héros
                file.write("\t\t\t\t\"nom\": \"AvancerEnFonctionDUnEvent\",")
                file.write("\t\t\t\t\"idEventObserve\": 0")
              when 3
                # Prédéfini
                # TODO
                #...
                #page.move_route
              end
              file.write("\t\t\t}")
              write_linebreak(file)
              file.write("\t\t\t],")
              write_linebreak(file)
            end
            # Fin des mouvements de la page
            
            
            # Ecriture des propriétés de la page
            
            # Apparence
            if page.graphic.character_name.to_s.empty?
              # L'apparence est un tile
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
            
            # Opacité
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
            if page.graphic.direction != 0
              file.write(sprintf("\t\t\t\"directionInitiale\": %d,", page.graphic.direction))
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
              when 1 then 1
              when 2 then 2
              when 3 then 4
              when 4 then 8
              when 5 then 16
            end
            file.write(sprintf("\t\t\t\"vitesse\": %d,", vitesse))
            write_linebreak(file)
            
            # Frequence
            frequence = case page.move_frequency
              when 0 then 32
              when 1 then 16
              when 2 then 8
              when 3 then 4
              when 4 then 2
              when 5 then 1
            end
            file.write(sprintf("\t\t\t\"frequence\": %d,", frequence))
            write_linebreak(file)
            
            # Animé en mouvement
            file.write(sprintf("\t\t\t\"animeEnMouvement\": %s,", page.walk_anime))
            write_linebreak(file)
            
            # Animé à l'arrêt
            file.write(sprintf("\t\t\t\"animeALArret\": %s,", page.step_anime))
            write_linebreak(file)
            
            # Direction fixe
            file.write(sprintf("\t\t\t\"directionFixe\": %s,", page.direction_fix))
            write_linebreak(file)
            
            # Traversable
            file.write(sprintf("\t\t\t\"directionFixe\": %s,", page.through))
            write_linebreak(file)
            
            # Au dessus de tout
            file.write(sprintf("\t\t\t\"auDessusDeTout\": %s", page.always_on_top))
            write_linebreak(file)
            # Fin des propriétés de la page
          
            
          # Fin de l'écriture de la page
          file.write("\t\t}")
          if i < event.pages.size - 1
              file.write(",")
          end
          write_linebreak(file)
            
          # On rajoute ça pour ne pas que le programme plante lol
          Graphics.update
          
          end
          # Fin du parcours des pages
          file.write("\t\t]")
        end
        write_linebreak(file)
        
        
        # Fin de l'écriture de l'event
        file.write("\t}")
        if event_id != map.events.keys.last
          file.write(",")
        end
        write_linebreak(file)
        
      end
      # Fin du parcours de events
      file.write("\t]")
      write_linebreak(file)
    
    # Fin de l'écriture de la map
    file.write("}")
    write_linebreak(file)
    
    # On referme le fichier de la map
    file.close
    
    end
    # Fin du parcours des maps
    
  end
  # Fin de la fonction principale
  
  
  #-----------------------------------------
  # Récupérer les commandes évènementielles
  #-----------------------------------------
  def write_commands(file, page)
    # S'il y a des commandes
    if page.list != nil && page.list.length > 0
      file.write("\t\t\t\"commandes\": [")
      write_linebreak(file)

      
      # Numérotation des choix
      nombre_de_choix_rencontres = 0
      choix_en_cours = Array.new
      
      # Numérotation des conditions
      nombre_de_conditions_rencontrees = 0
      conditions_en_cours = Array.new
      
      
      # S'intéresser aux event_data.code pour exporter d'autres types de commandes
      @command_id = 0
      while page.list[@command_id] != nil
        
        commande = page.list[@command_id]
        
        # Faut-il masquer cette commande ?
        case commande.code 
        when 0, 401, 408
          masquer_la_commande = true
        else
          masquer_la_commande = false
          file.write("\t\t\t{")
          write_linebreak(file)
        end
      
        case commande.code
        when 0
          # RM met des commandes de code 0 à chaque embranchement
        when 101
          # Message
          file.write("\t\t\t\t\"nom\": \"Message\",")
          write_linebreak(file)
          
          #récupération du texte multiligne
          meme_message = true
          ligne_message = 0
          texte = ""
          while ligne_message < 4 && meme_message
            commande_suivante = page.list[@command_id+ligne_message]
            if ligne_message == 0 || commande_suivante.code == 401
              # La commande suivante concerne le même message
              if ligne_message > 0
                texte += "\\n"
              end
              texte += commande_suivante.parameters[0]
            else
              # La commande suivante ne concerne plus le même message
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
          
          # Ajouter un nouvel élément à la pile
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
          
          # Retirer le dernier élément de la pile
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
          
          #récupération du commentaire multiligne
          meme_commentaire = true
          ligne_commentaire = 0
          texte = ""
          while ligne_commentaire < 4 && meme_commentaire
            commande_suivante = page.list[@command_id+ligne_commentaire]
            if ligne_commentaire == 0 || commande_suivante.code == 408
              # La commande suivante concerne le même commentaire
              if ligne_commentaire > 0
                texte += "\\n"
              end
              texte += commande_suivante.parameters[0]
            else
              # La commande suivante ne concerne plus le même message
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
            # Condition chronomètre
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
          end

          file.write(sprintf("\t\t\t\t\"numero\": %d", nombre_de_conditions_rencontrees))
          write_linebreak(file)
          
          conditions_en_cours << nombre_de_conditions_rencontrees
          nombre_de_conditions_rencontrees += 1
          
          #TODO
          #...
=begin
        #when 111
          # Condition si
        #when 411
          #Condition sinon
        when 112 # Boucle
        when 413 # Fin de boucle
        When 113 # Sortir de la boucle
        When 115 # Arrêter l'event
        When 116 # Effacer l'event
        When 117 # Appeler un code commun
        When 118 # Etiquette
        When 119 # Aller vers l'étiquette
        When 121 # Modifier interrupteur
        When 122 # Modifier variable
        When 123 # Modifier interrupteur local
        When 124 # Chronomètre
        When 125 # Ajouter argent
        When 126 # Ajouter objet
        When 127 # Ajouter arme
        When 128 # Ajouter equipement
        When 129 # Swap actors
        When 131 # Changer l'apparence de la boite de dialogues
        When 132 # Change Battle BGM
        When 133 # Change battle end ME
        When 134 # Autoriser la sauvegarde
        When 135 # Autoriser le menu
        When 136 # Changing prohibition of encounter
        
        When 201 # Changer de map
        When 202 # Repositionner un event
        When 203 # Faire défiler la map
        When 204 # Brouillard et autres paramètres de la map
        When 205 # Ton du brouillard
        When 206 # Opacité du brouillard
        When 207 # Animation
        When 209 # Déplacement
        When 210 # Attendre la fin du déplacement
        When 221 # Transition preparation
        When 222 # Transition execution
        When 223 # Ton de l'écran
        When 224 # Flasher l'écran => bof...
        When 225 # Tremblement de terre
        When 231 # Afficher image
        When 232 # Deplacer image
        When 233 # Tourner image
        When 235 # Effacer image
        When 236 # Météo
        When 241 # BGM
        When 242 # Fondu BGM
        When 245 # BGS
        When 246 # Fondu BGS
        When 249 # ME
        When 250 # SE
        When 251 # Arrêter SE

        When 303 # Rentrer un mot
        When 311 # Modifier la vie
        When 318 # Ajouter / Retirer Quête
        When 319 # Equipement

        When 351 # Ouvrir le menu
        When 352 # Ouvrir le menu de sauvegarde
        When 353 # Game over
        When 354 # Title screen
        When 355 # script
=end
        else
          # Commande inconnue
          file.write(sprintf("\t\t\t\t\"nom\": \"%d\",", commande.code))
          write_linebreak(file)
          
          # Paramètres
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
        
        if !masquer_la_commande
          file.write("\t\t\t}")
          if @command_id < page.list.size - 1
            file.write(",")
          end
          write_linebreak(file)
        end
      
        @command_id += 1
      end

      file.write("\t\t\t],")
      write_linebreak(file)
    end
  end
  

  #----------------------------------------------------------
  # Traduction du tile_id de RM vers celui de Kujira no Java
  #----------------------------------------------------------
  def normaliser_tile_id(tile_id)
    tile_id = tile_id - 384 # On décale les autotiles dans les négatifs
    if tile_id < 0
      # RPG Maker différencie les 48 apparences possibles d'un autotile
      tile_id = tile_id/48 - 1
      # On décale les autotiles pour que -1 représente la case vide
      if tile_id == -9
        tile_id = -1
      end
    end
    return tile_id
  end
  
  
  # Fin de la classe
end
