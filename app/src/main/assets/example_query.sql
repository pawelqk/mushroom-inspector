SELECT r.id, r.label, r.description, n.name 
FROM mushrooms_search_result r
JOIN mushroom_edibility e ON r.id = e.id 
JOIN edibility_names n ON e.edibility_id = n.id 
WHERE label = "Boletus edulis";
